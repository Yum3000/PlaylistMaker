package com.example.playlistmaker.media.presentation

sealed class MediaScreenFavouritesState {

    data object Content: MediaScreenFavouritesState()
    data object Empty: MediaScreenFavouritesState()
}