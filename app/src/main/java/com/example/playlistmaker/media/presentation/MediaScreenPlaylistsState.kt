package com.example.playlistmaker.media.presentation

sealed class MediaScreenPlaylistsState {

    data object Content: MediaScreenPlaylistsState()
    data object Empty: MediaScreenPlaylistsState()
}