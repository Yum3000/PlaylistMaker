package com.example.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.appbar.MaterialToolbar
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val musicAPIService = retrofit.create<MusicAPIService>()

    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    private var inputedText: String = ""
    private var searchLineFocus: Boolean = false
    private lateinit var inputSearchLine: EditText
    private var lastSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholder_message)
        trackList = findViewById(R.id.recyclerView)
        val messageView = findViewById<View>(R.id.message_view)

        adapter.tracks = tracks

        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = adapter

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        inputSearchLine = findViewById(R.id.search_edit_text)

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
            searchLineFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)
        }

        inputSearchLine.setText(inputedText)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputSearchLine.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputSearchLine.windowToken, 0)
            inputSearchLine.clearFocus()
            searchLineFocus = false
            tracks.clear()
        }

        inputSearchLine.setOnFocusChangeListener { _, hasFocus ->
            searchLineFocus = hasFocus
        }

        inputSearchLine.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                if (inputSearchLine.text.isNotEmpty()) {
                    lastSearchQuery = inputSearchLine.text.toString()
                    executeRequest(messageView, lastSearchQuery.toString())
                }
            }
            false
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputSearchLine.addTextChangedListener(searchTextWatcher)

        if (searchLineFocus || savedInstanceState == null) {
            inputSearchLine.requestFocus()
            inputSearchLine.setSelection(inputSearchLine.text.length)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(inputSearchLine, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, inputSearchLine.text.toString())
        outState.putBoolean(SEARCH_FOCUS, searchLineFocus)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
        inputSearchLine.setText(inputedText)
        searchLineFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)

        if (searchLineFocus) {
            inputSearchLine.requestFocus()
            inputSearchLine.setSelection(inputSearchLine.text.length)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(inputSearchLine, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_FOCUS = "SEARCH_FOCUS"
    }

    private fun executeRequest(messageView: View, inputQuery: String) {
        musicAPIService.searchTracks(inputQuery)
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    tracks.clear()
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                        }
                        if (tracks.isEmpty()) {
                            showPlaceholder(messageView, getString(R.string.nothings_found), false, inputQuery)
                            trackList.visibility = View.GONE
                        } else {
                            trackList.visibility = View.VISIBLE
                            messageView.visibility = View.GONE
                        }
                    } else {
                        showPlaceholder(messageView, getString(R.string.smth_wrong), true, inputQuery)
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showPlaceholder(messageView, getString(R.string.smth_wrong), true, inputQuery)
                }
            })
    }

    private fun showPlaceholder(messageView: View, message: String, connectionFailed: Boolean, lastSearchQuery: String) {
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
}
