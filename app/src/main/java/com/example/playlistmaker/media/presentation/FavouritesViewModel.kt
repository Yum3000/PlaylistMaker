package com.example.playlistmaker.media.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.media.domain.db.FavTracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val context: Context,
    private val favTracksInteractor: FavTracksInteractor
) : ViewModel() {
    private val stateFavouritesScreen = MutableLiveData<MediaScreenFavouritesState>()
    fun observeStateFavourites(): LiveData<MediaScreenFavouritesState> = stateFavouritesScreen

    private val trackIdToOpenPlayer = SingleLiveEvent<Int>()
    fun getTrackIdToOpenPlayer(): LiveData<Int> = trackIdToOpenPlayer

    private var tracks: List<Track> = emptyList()

    private val handleTrackClickDebounced = debounce<Int>(
        CLICK_TRACK_DEBOUNCE_DELAY, viewModelScope, false
    ) { trackId ->
        val track = tracks.find { it.trackId == trackId }
        if (track != null) {
            trackIdToOpenPlayer.postValue(trackId)
        }
    }

    fun handleTrackClick(trackId: Int) {
        handleTrackClickDebounced(trackId)
    }

    init {
        renderState(MediaScreenFavouritesState.Loading)
        updateFavouriteList()
    }

    fun processResult(trackList: List<Track>) {
        tracks = trackList
        if (tracks.isEmpty()) {
            renderState(MediaScreenFavouritesState.Empty(context.getString(R.string.no_favourites)))
        } else {
            renderState(MediaScreenFavouritesState.Content(tracks))
        }
    }

    fun renderState(state: MediaScreenFavouritesState) {
        stateFavouritesScreen.postValue((state))
    }

    fun updateFavouriteList() {
        viewModelScope.launch(Dispatchers.IO) {
            favTracksInteractor
                .getFavTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    companion object {
        private const val CLICK_TRACK_DEBOUNCE_DELAY = 1000L
    }
}