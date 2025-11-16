package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.example.playlistmaker.ErrorMessage
import com.example.playlistmaker.ProgressBar
import com.example.playlistmaker.R
import com.example.playlistmaker.TrackList
import com.example.playlistmaker.media.presentation.FavouritesViewModel
import com.example.playlistmaker.media.presentation.MediaScreenFavouritesState
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavouritesScreen(
    openAudioPlayerScreen: (trackId: Int) -> Unit,
    viewModel: FavouritesViewModel = koinViewModel(),) {
    val screenState by viewModel.observeStateFavourites().observeAsState()

    when (screenState) {
        is MediaScreenFavouritesState.Content -> {
            TrackList((screenState as MediaScreenFavouritesState.Content).tracks) {
                openAudioPlayerScreen(it)
            }
        }

        is MediaScreenFavouritesState.Empty -> {
            ErrorMessage(
                stringResource(R.string.no_favourites),
                getPlaceholderImageResource(),
                106
            )
        }

        is MediaScreenFavouritesState.Loading -> {
            ProgressBar()
        }

        null -> {}
    }
}

@Composable
private fun getPlaceholderImageResource(): Int {
    val isNightMode = isSystemInDarkTheme()

    return if (isNightMode) {
        R.drawable.no_results_icon_dark
    } else {
        R.drawable.no_results_icon_light
    }
}