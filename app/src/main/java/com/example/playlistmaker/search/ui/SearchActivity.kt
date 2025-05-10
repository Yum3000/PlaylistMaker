package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.SearchTrackInfo
import com.example.playlistmaker.search.ui.SearchViewModel.Companion.INTENT_TRACK_KEY

class SearchActivity : ComponentActivity() {

    private lateinit var viewModel: SearchViewModel

    private lateinit var binding: ActivitySearchBinding

    private val searchAdapter = TrackAdapter(mutableListOf()) { track, _ ->
        viewModel.handleTrackClick(track.trackId)
    }

    private val historyAdapter = TrackAdapter(mutableListOf()) { track, _ ->
        viewModel.handleTrackClick(track.trackId)
    }

    private var inputedText: String = ""
    private var searchFieldFocus: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        binding.recyclerView.adapter = searchAdapter

        binding.recyclerViewHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewHistory.adapter = historyAdapter

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        val liveData = viewModel.getSearchStateLiveData()
        when (val liveDataValue = liveData.value) {
            is SearchScreenState.Loading -> showLoading()
            is SearchScreenState.History -> showHistory(liveDataValue.tracks)
            is SearchScreenState.Content -> showContent(liveDataValue.tracks)
            is SearchScreenState.Error -> showError(liveDataValue.query)
        }
        liveData.observe(this) { state ->
            when (state) {
                is SearchScreenState.Loading -> showLoading()
                is SearchScreenState.History -> showHistory(state.tracks)
                is SearchScreenState.Content -> showContent(state.tracks)
                is SearchScreenState.Error -> showError(state.query)
            }
        }

        viewModel.getTrackIdToOpenPlayer().observe(this) { trackId -> openPlayerActivity(trackId) }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.handleSearchTextFocus(hasFocus)
        }

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
            searchFieldFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)
        }

        binding.searchEditText.setText(inputedText)

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
            binding.searchEditText.clearFocus()
            searchFieldFocus = false
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                viewModel.handleSearchChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.searchEditText.addTextChangedListener(searchTextWatcher)

//        if (searchFieldFocus || savedInstanceState == null) {
//            binding.searchEditText.requestFocus()
//            binding.searchEditText.setSelection(binding.searchEditText.text.length)
//            val inputMethodManager =
//                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//            inputMethodManager?.showSoftInput(
//                binding.searchEditText,
//                InputMethodManager.SHOW_IMPLICIT
//            )
//        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, binding.searchEditText.text.toString())
        outState.putBoolean(SEARCH_FOCUS, searchFieldFocus)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
        binding.searchEditText.setText(inputedText)
        searchFieldFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)

        if (searchFieldFocus) {
            binding.searchEditText.requestFocus()
            binding.searchEditText.setSelection(binding.searchEditText.text.length)
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(
                binding.searchEditText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }
    }

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_FOCUS = "SEARCH_FOCUS"
    }

    private fun openPlayerActivity(trackId: Int) {
        if (trackId < 0) return
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(INTENT_TRACK_KEY, trackId)
        startActivity(intent)
    }

    private fun hideSearchHistory() {
        binding.historyLayout.visibility = View.GONE
    }

    private fun showPlaceholder(
        message: String,
        connectionFailed: Boolean,
        lastSearchQuery: String = ""
    ) {
        binding.recyclerView.visibility = View.GONE
        binding.messageView.root.visibility = View.VISIBLE
        binding.messageView.placeholderMessage.text = message

        val imageResource = getPlaceholderImageResource(connectionFailed)
        binding.messageView.placeholderImage.setImageResource(imageResource)

        if (connectionFailed) {
            binding.messageView.placeholderButton.visibility = View.VISIBLE
            binding.messageView.placeholderButton.setOnClickListener {
                viewModel.executeRequest(lastSearchQuery)
            }
        } else {
            binding.messageView.placeholderButton.visibility = View.GONE
        }
    }

    private fun hidePlaceholder() {
        binding.messageView.root.visibility = View.GONE
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

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        hidePlaceholder()
        hideSearchHistory()
        showProgressBar()
    }

    private fun showHistory(tracks: List<SearchTrackInfo>) {
        hideProgressBar()
        historyAdapter.tracks.clear()
        historyAdapter.tracks.addAll(tracks)
        historyAdapter.notifyDataSetChanged()
        binding.historyLayout.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<SearchTrackInfo>) {
        hideProgressBar()
        searchAdapter.tracks.clear()
        if (tracks.isEmpty()) {
            showPlaceholder(
                getString(R.string.nothings_found),
                false,
            )
            binding.recyclerView.visibility = View.GONE
        } else {
            searchAdapter.tracks.addAll(tracks)
            binding.recyclerView.visibility = View.VISIBLE
            binding.messageView.root.visibility = View.GONE
        }
        searchAdapter.notifyDataSetChanged()
    }

    private fun showError(lastSearchQuery: String) {
        hideProgressBar()
        hideSearchHistory()
        showPlaceholder(
            getString(R.string.smth_wrong),
            true,
            lastSearchQuery
        )
    }
}