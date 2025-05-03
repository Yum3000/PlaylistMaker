package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.App.Companion.INTENT_TRACK_KEY
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private val musicAPIService = RetrofitClient.musicAPIService

    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView

    private val tracks: MutableList<Track> = mutableListOf()
    private lateinit var searchHistory: SearchHistory

    private val searchAdapter = TrackAdapter(tracks) { track, _ ->
        openAudioPlayer(track)
    }

    private var inputedText: String = ""
    private var searchFieldFocus: Boolean = false
    private lateinit var searchField: EditText
    private var messageView: View? = null
    private lateinit var progressBar: ProgressBar
    private var isTrackClickAllowed: Boolean = true

    private val searchRunnable = Runnable { messageView?.let { executeRequest(it, inputedText) } }

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholder_message)
        trackList = findViewById(R.id.recyclerView)
        messageView = findViewById(R.id.message_view)
        progressBar = findViewById(R.id.progressBar)

        trackList.adapter = searchAdapter

        searchHistory = SearchHistory((applicationContext as App).sharedPref)
        val historyLayout = findViewById<LinearLayout>(R.id.historyLayout)
        val historyTrackList = findViewById<RecyclerView>(R.id.recyclerViewHistory)
        val historyClearButton = findViewById<Button>(R.id.clearHistory)

        val historyAdapter = TrackAdapter(searchHistory.history) { track, adapter ->
            openAudioPlayer(track)
            showSearchHistory(adapter, historyLayout)
        }
        historyTrackList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyTrackList.adapter = historyAdapter

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        searchField = findViewById(R.id.search_edit_text)
        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (searchField.text.isEmpty() && hasFocus) {
                showSearchHistory(historyAdapter, historyLayout)
            } else {
                hideSearchHistory(historyAdapter, historyLayout)
            }
        }

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
            searchFieldFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)
        }

        searchField.setText(inputedText)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            searchField.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchField.windowToken, 0)
            searchField.clearFocus()
            searchFieldFocus = false
            tracks.clear()
            searchAdapter.notifyDataSetChanged()
            messageView?.let { view ->
                hidePlaceholder(view)
            }
            showSearchHistory(historyAdapter, historyLayout)
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                inputedText = s.toString()
                if (s.isNullOrEmpty()) {
                    tracks.clear()
                    messageView?.let { view ->
                        hidePlaceholder(view)
                    }
                    if (searchField.hasFocus()){
                        showSearchHistory(historyAdapter, historyLayout)
                    }
                } else {
                    hideSearchHistory(historyAdapter, historyLayout)
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchField.addTextChangedListener(searchTextWatcher)

        if (searchFieldFocus || savedInstanceState == null) {
            searchField.requestFocus()
            searchField.setSelection(searchField.text.length)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
        }

        historyClearButton.setOnClickListener {
            searchHistory.clearHistory()
            hideSearchHistory(historyAdapter, historyLayout)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchField.text.toString())
        outState.putBoolean(SEARCH_FOCUS, searchFieldFocus)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
        searchField.setText(inputedText)
        searchFieldFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)

        if (searchFieldFocus) {
            searchField.requestFocus()
            searchField.setSelection(searchField.text.length)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_FOCUS = "SEARCH_FOCUS"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_TRACK_DEBOUNCE_DELAY = 1000L
    }

    private fun showSearchHistory(historyAdapter: TrackAdapter, historyLayout: LinearLayout) {
        if (searchHistory.history.isEmpty()) return

        historyAdapter.notifyDataSetChanged()
        historyLayout.visibility = View.VISIBLE
    }

    private fun hideSearchHistory(historyAdapter: TrackAdapter, historyLayout: LinearLayout) {
        historyAdapter.notifyDataSetChanged()
        historyLayout.visibility = View.GONE
    }

    private fun executeRequest(messageView: View, inputQuery: String) {
        placeholderMessage.visibility = View.GONE
        trackList.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        musicAPIService.searchTracks(inputQuery)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    tracks.clear()
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                        }
                        if (tracks.isEmpty()) {
                            showPlaceholder(
                                messageView,
                                getString(R.string.nothings_found),
                                false,
                                inputQuery
                            )
                            trackList.visibility = View.GONE
                        } else {
                            trackList.visibility = View.VISIBLE
                            messageView.visibility = View.GONE
                        }
                    } else {
                        showPlaceholder(
                            messageView,
                            getString(R.string.smth_wrong),
                            true,
                            inputQuery
                        )
                    }
                    searchAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showPlaceholder(messageView, getString(R.string.smth_wrong), true, inputQuery)
                }
            })
    }

    private fun showPlaceholder(
        messageView: View,
        message: String,
        connectionFailed: Boolean,
        lastSearchQuery: String
    ) {
        trackList.visibility = View.GONE
        messageView.visibility = View.VISIBLE
        messageView.findViewById<TextView>(R.id.placeholder_message).text = message
        val placeholderImage = messageView.findViewById<ImageView>(R.id.placeholder_image)
        val placeholderButton = messageView.findViewById<Button>(R.id.placeholder_button)

        val imageResource = getPlaceholderImageResource(connectionFailed)
        placeholderImage.setImageResource(imageResource)

        if (connectionFailed) {
            placeholderButton.visibility = View.VISIBLE
            placeholderButton.setOnClickListener {
                executeRequest(messageView, lastSearchQuery)
            }
        } else {
            placeholderButton.visibility = View.GONE
        }
    }

    private fun hidePlaceholder(messageView: View) {
        messageView.visibility = View.GONE
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    private fun getPlaceholderImageResource(connectionFailed: Boolean): Int {
        return if (connectionFailed) {
            if (isNightMode()) {
                R.drawable.no_connection_icon_dark
            } else {
                R.drawable.no_connection_icon_light
            }
        } else {
            if (isNightMode()) {
                R.drawable.no_results_icon_dark
            } else {
                R.drawable.no_results_icon_light
            }
        }
    }

    private fun openAudioPlayer(track: Track) {
        if (trackClickDebounce()) {
            searchHistory.updateHistory(track)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(INTENT_TRACK_KEY, track)
            startActivity(intent)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun trackClickDebounce() : Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, CLICK_TRACK_DEBOUNCE_DELAY)
        }
        return current
    }
}