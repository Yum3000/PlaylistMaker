package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavouritesViewModel(): ViewModel() {
    private val stateFavouritesScreen = MutableLiveData<MediaScreenFavouritesState>(
        MediaScreenFavouritesState.Empty)
    fun observeStateFavourites(): LiveData<MediaScreenFavouritesState> = stateFavouritesScreen
}