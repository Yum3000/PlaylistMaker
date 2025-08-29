package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface MediaScreenPlaylistsState {

    object Loading: MediaScreenPlaylistsState
    data class Content(val playlists: List<Playlist>): MediaScreenPlaylistsState
    data class Empty(val message: String): MediaScreenPlaylistsState
}