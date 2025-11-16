package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.Button
import com.example.playlistmaker.ErrorMessage
import com.example.playlistmaker.Playlists
import com.example.playlistmaker.R
import com.example.playlistmaker.media.presentation.MediaScreenPlaylistsState
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(
    openToModifyPlaylistScreen: (playlistId: Int) -> Unit,
    openToCreateNewPlaylistScreen: () -> Unit,
    viewModel: PlaylistsViewModel = koinViewModel()
) {
    val screenState by viewModel.observeStatePlaylists().observeAsState()

    LaunchedEffect(Unit) {
        viewModel.updatePlaylists()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(top = 24.dp))
        Button(stringResource(R.string.new_playlist)) {
            openToCreateNewPlaylistScreen
        }

        when (screenState) {
            is MediaScreenPlaylistsState.Content -> {
                Playlists(
                    playlists = (screenState as MediaScreenPlaylistsState.Content).playlists,
                    onClick = openToModifyPlaylistScreen
                )
            }

            is MediaScreenPlaylistsState.Empty -> {
                ErrorMessage(
                    message = stringResource(R.string.no_playlists),
                    iconId = R.drawable.no_results_icon_dark, // убрать
                    topPaddingDp = 46
                )
            }

            null -> {}
        }
    }
}