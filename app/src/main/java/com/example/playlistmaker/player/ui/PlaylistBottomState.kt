package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistBottomState {
    data class Content(val playlists: List<Playlist>) : PlaylistBottomState
    data class Empty(val message: String) : PlaylistBottomState
    data class Error(val message: String): PlaylistBottomState
}