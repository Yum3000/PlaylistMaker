package com.example.playlistmaker.media.presentation

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val context: Context,
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {
    private val statePlaylistsScreen = MutableLiveData<MediaScreenPlaylistsState>()
    fun observeStatePlaylists(): LiveData<MediaScreenPlaylistsState> = statePlaylistsScreen

    private var lists: List<Playlist> = emptyList()

    init {
        renderState(MediaScreenPlaylistsState.Loading)
        updatePlaylists()
    }

    fun renderState(state: MediaScreenPlaylistsState) {
        statePlaylistsScreen.postValue(state)
    }

    fun updatePlaylists() {
        viewModelScope.launch (Dispatchers.IO) {
            playlistsInteractor.getPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    fun processResult(playlists: List<Playlist>) {
        lists = playlists
        if (lists.isEmpty()) {
            renderState(MediaScreenPlaylistsState.Empty(
                context.getString(R.string.no_playlists)))
        } else {
            renderState(MediaScreenPlaylistsState.Content(lists))
        }
    }

}