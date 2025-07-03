package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsViewModel(): ViewModel() {
    private val statePlaylistsScreen = MutableLiveData<MediaScreenPlaylistsState>(
        MediaScreenPlaylistsState.Empty)
    fun observeStatePlaylists(): LiveData<MediaScreenPlaylistsState> = statePlaylistsScreen
}