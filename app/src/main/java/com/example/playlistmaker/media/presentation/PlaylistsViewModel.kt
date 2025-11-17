package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.utils.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val resourceProvider: ResourceProvider,
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {
    private val statePlaylistsScreen = MutableLiveData<MediaScreenPlaylistsState>()
    fun observeStatePlaylists(): LiveData<MediaScreenPlaylistsState> = statePlaylistsScreen

    private val playlistIdToShowContent = SingleLiveEvent<Int>()
    fun getPlaylistIdToShowContent(): LiveData<Int> = playlistIdToShowContent

    private var lists: List<Playlist> = emptyList()

    private fun renderState(state: MediaScreenPlaylistsState) {
        statePlaylistsScreen.postValue(state)
    }

    fun updatePlaylists() {
        viewModelScope.launch (Dispatchers.IO) {
            playlistsInteractor.getPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        lists = playlists
        if (lists.isEmpty()) {
            renderState(MediaScreenPlaylistsState.Empty(
                resourceProvider.getString(R.string.no_playlists)))
        } else {
            renderState(MediaScreenPlaylistsState.Content(lists))
        }
    }

    fun handleViewCreated() {
        updatePlaylists()
    }

    fun handlePlaylistClick(playlistId: Int) {
        playlistIdToShowContent.postValue(playlistId)
    }
}