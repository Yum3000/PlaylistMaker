package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val historyInteractor: TracksHistoryInteractor,
) : ViewModel() {

    val searchTracks: MutableList<Track> = mutableListOf()

    private val searchStateLiveData = MutableLiveData<SearchScreenState>()
    fun getSearchStateLiveData(): LiveData<SearchScreenState> = searchStateLiveData

    private val trackIdToOpenPlayer = SingleLiveEvent<Int>()
    fun getTrackIdToOpenPlayer(): LiveData<Int> = trackIdToOpenPlayer

    private val handleTrackClickDebounced = debounce<Int> (
        CLICK_TRACK_DEBOUNCE_DELAY, viewModelScope, false) { trackId ->
        val track = searchTracks.find { it.trackId == trackId }
        if (track != null) {
            historyInteractor.updateHistory(track)
            trackIdToOpenPlayer.postValue(trackId)
        }
    }

    fun handleTrackClick(trackId: Int) {
        handleTrackClickDebounced(trackId)
    }

    private val handleHistoryTrackClickDebounced = debounce<Int> (
        CLICK_TRACK_DEBOUNCE_DELAY, viewModelScope, false) { trackId ->
        viewModelScope.launch {
            val track = historyInteractor.getHistory().find { it.trackId == trackId }
            if (track != null) {
                historyInteractor.updateHistory(track)
                trackIdToOpenPlayer.postValue(trackId)
            }
        }
    }

    fun handleHistoryTrackClick(trackId: Int) {
        handleHistoryTrackClickDebounced(trackId)
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        searchStateLiveData.postValue(SearchScreenState.History(listOf()))
    }

    private val handleSearchChangeDebounced = debounce<String>(
        SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { query ->
        if (query.isEmpty()) {
            viewModelScope.launch {
                searchTracks.clear()
                val tracks =
                    historyInteractor.getHistory().map { ListTrackInfo.trackToListTrackInfo(it) }
                searchStateLiveData.postValue(SearchScreenState.History(tracks))
            }
        } else {
                searchStateLiveData.postValue(SearchScreenState.Loading)
                executeRequest(query)
            }
    }

    fun handleSearchChange(s: String) {
        handleSearchChangeDebounced(s)
    }

    fun executeRequest(inputQuery: String) {
         viewModelScope.launch (Dispatchers.IO) {
             trackInteractor
                 .searchTracks(inputQuery)
                 .collect { tracks ->
                     processSearchTracks(tracks, inputQuery)
                 }
         }
    }

    private fun processSearchTracks(foundTracks: List<Track>?, inputQuery: String) {
        searchTracks.clear()
        if (foundTracks != null) {
            searchTracks.addAll(foundTracks)
            val tracks = searchTracks.map { ListTrackInfo.trackToListTrackInfo(it) }
            searchStateLiveData.postValue(SearchScreenState.Content(tracks, inputQuery))
        } else {
            searchStateLiveData.postValue(SearchScreenState.Error(inputQuery))
        }
    }

    fun handleSearchTextFocus(focused: Boolean) {
        if (focused) {
            viewModelScope.launch {
                val tracks = historyInteractor.getHistory().map { ListTrackInfo.trackToListTrackInfo(it) }
                searchStateLiveData.postValue(SearchScreenState.History(tracks))
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_TRACK_DEBOUNCE_DELAY = 1000L
    }
}