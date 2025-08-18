package com.example.playlistmaker.media.presentation

import com.example.playlistmaker.search.domain.models.Track

sealed interface MediaScreenFavouritesState {

    object Loading: MediaScreenFavouritesState

    data class Content(val tracks: List<Track>): MediaScreenFavouritesState
    data class Empty(val message: String): MediaScreenFavouritesState

}