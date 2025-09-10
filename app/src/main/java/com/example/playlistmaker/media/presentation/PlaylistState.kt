package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.media.domain.models.PlaylistDetails

sealed interface PlaylistState {
    data class Content(val playlistDetails: PlaylistDetails) : PlaylistState
}