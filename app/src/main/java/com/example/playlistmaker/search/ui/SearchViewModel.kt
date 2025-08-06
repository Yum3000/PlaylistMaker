package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.SearchTrackInfo
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val trackInteractor: TracksInteractor,
    private val historyInteractor: TracksHistoryInteractor,
) : ViewModel() {

    val searchTracks: MutableList<Track> = mutableListOf()

    private var isTrackClickAllowed: Boolean = true

    private val handler = Handler(Looper.getMainLooper())

    private var inputedText = ""
    private val searchRunnable = Runnable {
        if (inputedText.isNotEmpty()) {
            executeRequest(inputedText)
        }
    }

    private val searchStateLiveData = MutableLiveData<SearchScreenState>()
    fun getSearchStateLiveData(): LiveData<SearchScreenState> = searchStateLiveData

    private val trackIdToOpenPlayer = SingleLiveEvent<Int>()
    fun getTrackIdToOpenPlayer(): LiveData<Int> = trackIdToOpenPlayer

    fun handleTrackClick(trackId: Int) {
        val track = searchTracks.find { it.trackId == trackId }
        if (trackClickDebounce() && track != null) {
            historyInteractor.updateHistory(track)
            trackIdToOpenPlayer.postValue(trackId)
        }
    }

    fun handleHistoryTrackClick(trackId: Int) {
        val track = historyInteractor.getHistory().find { it.trackId == trackId }
        if (trackClickDebounce() && track != null) {
            historyInteractor.updateHistory(track)
            trackIdToOpenPlayer.postValue(trackId)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun trackClickDebounce(): Boolean {
        val current = isTrackClickAllowed
        if (isTrackClickAllowed) {
            isTrackClickAllowed = false
            handler.postDelayed({ isTrackClickAllowed = true }, CLICK_TRACK_DEBOUNCE_DELAY)
        }
        return current
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

    fun handleSearchChange(s: String) {
        inputedText = s
        handler.removeCallbacks(searchRunnable)
        if (s.isEmpty()) {
            searchTracks.clear()
            val tracks = historyInteractor.getHistory().map { trackToSearchTrackInfo(it) }
            searchStateLiveData.postValue(SearchScreenState.History(tracks))
        } else {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
            searchStateLiveData.postValue(SearchScreenState.Loading)
        }

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
        const val INTENT_TRACK_KEY = "track_to_player"
    }
}