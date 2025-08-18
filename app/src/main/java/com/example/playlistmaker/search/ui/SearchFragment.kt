package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private val searchAdapter = TrackAdapter(mutableListOf()) { track, _ ->
        viewModel.handleTrackClick(track.trackId)
    }

    private val historyAdapter = TrackAdapter(mutableListOf()) { track, _ ->
        viewModel.handleHistoryTrackClick(track.trackId)
    }

    private var inputedText: String = ""
    private var searchFieldFocus: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = searchAdapter

        binding.recyclerViewHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewHistory.adapter = historyAdapter


        viewModel.getSearchStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> this.showProgressBar()
                is SearchScreenState.History -> showHistory(state.tracks)
                is SearchScreenState.Content -> showContent(state.tracks)
                is SearchScreenState.Error -> showError(state.query)
            }
        }

        viewModel.getTrackIdToOpenPlayer().observe(viewLifecycleOwner) {
                trackId -> openPlayerActivity(trackId)
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            viewModel.handleSearchTextFocus(hasFocus)
            searchFieldFocus = hasFocus
        }

        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
            searchFieldFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)
        }

        binding.searchEditText.setText(inputedText)

        binding.clearIcon.setOnClickListener {
            binding.searchEditText.setText("")
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

        if (searchFieldFocus || savedInstanceState == null) {
            binding.searchEditText.requestFocus()
            binding.searchEditText.setSelection(binding.searchEditText.text.length)
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(
                binding.searchEditText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, binding.searchEditText.text.toString())
        outState.putBoolean(SEARCH_FOCUS, searchFieldFocus)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            inputedText = savedInstanceState.getString(SEARCH_TEXT, "")
            searchFieldFocus = savedInstanceState.getBoolean(SEARCH_FOCUS, false)
        }
        binding.searchEditText.setText(inputedText)

        if (searchFieldFocus) {
            binding.searchEditText.requestFocus()
            binding.searchEditText.setSelection(binding.searchEditText.text.length)
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(
                binding.searchEditText,
                InputMethodManager.SHOW_IMPLICIT
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlayerActivity(trackId: Int) {
        val bundle = AudioPlayerFragment.createArgs(trackId) // Создаем Bundle с trackId
        findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment, bundle)
    }

    private fun hideSearchHistory() {
        binding.historyLayout.isVisible = false
    }

    private fun showPlaceholder(
        message: String,
        connectionFailed: Boolean,
        lastSearchQuery: String = ""
    ) {
        hideContent()
        binding.messageView.root.isVisible = true
        binding.messageView.placeholderMessage.text = message

        val imageResource = getPlaceholderImageResource(connectionFailed)
        binding.messageView.placeholderImage.setImageResource(imageResource)

        if (connectionFailed) {
            binding.messageView.placeholderButton.isVisible = true
            binding.messageView.placeholderButton.setOnClickListener {
                viewModel.executeRequest(lastSearchQuery)
            }
        } else {
            binding.messageView.placeholderButton.isVisible = false
        }
    }

    private fun hidePlaceholder() {
        binding.messageView.root.isVisible = false
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

    private fun hideProgressBar() {
        binding.progressBar.isVisible = false
    }

    private fun showProgressBar() {
        hideContent()
        hidePlaceholder()
        hideSearchHistory()
        binding.progressBar.isVisible = true
    }

    private fun showHistory(tracks: List<ListTrackInfo>) {
        hideProgressBar()
        hideContent()
        hidePlaceholder()
        if (tracks.isEmpty()) {
            binding.historyLayout.isVisible = false
        } else {
            historyAdapter.tracks.clear()
            historyAdapter.tracks.addAll(tracks)
            historyAdapter.notifyDataSetChanged()
            binding.historyLayout.isVisible = true
        }
    }

    private fun showContent(tracks: List<ListTrackInfo>) {
        hideProgressBar()
        hidePlaceholder()
        hideSearchHistory()
        searchAdapter.tracks.clear()
        if (tracks.isEmpty()) {
            showPlaceholder(
                getString(R.string.nothings_found),
                false,
            )
            hideContent()
        } else {
            searchAdapter.tracks.addAll(tracks)
            binding.recyclerView.isVisible = true
        }
        searchAdapter.notifyDataSetChanged()
    }

    private fun hideContent() {
        binding.recyclerView.isVisible = false
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

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_FOCUS = "SEARCH_FOCUS"
    }
}