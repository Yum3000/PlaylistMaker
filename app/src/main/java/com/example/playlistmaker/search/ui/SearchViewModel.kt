package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.SearchTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce

class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val historyInteractor: TracksHistoryInteractor,
) : ViewModel() {

    val searchTracks: MutableList<Track> = mutableListOf()

    private val handler = Handler(Looper.getMainLooper())

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
        val track = historyInteractor.getHistory().find { it.trackId == trackId }
        if (track != null) {
            historyInteractor.updateHistory(track)
            trackIdToOpenPlayer.postValue(trackId)
        }
    }

    fun handleHistoryTrackClick(trackId: Int) {
        handleHistoryTrackClickDebounced(trackId)
    }

    private fun trackToSearchTrackInfo(track: Track?): SearchTrackInfo {
        return SearchTrackInfo(
            track?.trackId ?: -1,
            track?.trackName ?: "",
            track?.artistName ?: "",
            track?.trackTimeMillis ?: "",
            track?.artworkUrl100 ?: ""
        )
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        searchStateLiveData.postValue(SearchScreenState.History(listOf()))
    }

    private val handleSearchChangeDebounced = debounce<String>(
        SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { querry ->
        if (querry.isEmpty()) {
            searchTracks.clear()
            val tracks = historyInteractor.getHistory().map { trackToSearchTrackInfo(it) }
            searchStateLiveData.postValue(SearchScreenState.History(tracks))
        } else {
            searchStateLiveData.postValue(SearchScreenState.Loading)
            executeRequest(querry)
        }
    }

    fun handleSearchChange(s: String) {
        handleSearchChangeDebounced(s)
    }

    fun executeRequest(inputQuery: String) {
        trackInteractor.searchTracks(inputQuery, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?) {
                handler.post {
                    searchTracks.clear()
                    if (foundTracks != null) {
                        searchTracks.addAll(foundTracks)
                        val tracks = searchTracks.map { trackToSearchTrackInfo(it) }
                        searchStateLiveData.postValue(SearchScreenState.Content(tracks, inputQuery))
                    } else {
                        searchStateLiveData.postValue(SearchScreenState.Error(inputQuery))
                    }
                }
            }
        })
    }

    fun handleSearchTextFocus(focused: Boolean) {
        if (focused) {
            val tracks = historyInteractor.getHistory().map { trackToSearchTrackInfo(it) }
            searchStateLiveData.postValue(SearchScreenState.History(tracks))
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_TRACK_DEBOUNCE_DELAY = 1000L
    }
}