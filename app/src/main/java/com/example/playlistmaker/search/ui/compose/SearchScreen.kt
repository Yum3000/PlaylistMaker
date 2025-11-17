package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.components.CustomTextField
import com.example.playlistmaker.components.ErrorMessage
import com.example.playlistmaker.components.ListOfListTrackInfo
import com.example.playlistmaker.components.ProgressBar
import com.example.playlistmaker.R
import com.example.playlistmaker.components.Toolbar
import com.example.playlistmaker.components.TrackListHistory
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(
    openAudioPlayerScreen: (trackId: Int) -> Unit,
    viewModel: SearchViewModel = koinViewModel()) {

    val searchState by viewModel.getSearchStateLiveData().observeAsState()
    val trackIdToOpenPlayer by viewModel.getTrackIdToOpenPlayer().observeAsState()

    LifecycleStartEffect(Unit) {
        viewModel.loadHistory()
        onStopOrDispose {}
    }

    LaunchedEffect(trackIdToOpenPlayer) {
        val trackId = trackIdToOpenPlayer

        if (trackId != null && trackId > -1) {
            viewModel.handleOpenTrack()
            openAudioPlayerScreen(trackId)
        }
    }

    Scaffold(topBar = {
        Toolbar(title = stringResource(R.string.search_text))
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.primaryBackgroundColor)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var searchFieldText by remember { mutableStateOf("") }

            CustomTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = searchFieldText,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search_icon),
                        tint = AppTheme.colors.secondaryTextColor,
                        contentDescription = stringResource(R.string.search_text),
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.clear_input_icon16),
                        tint = AppTheme.colors.secondaryTextColor,
                        contentDescription = stringResource(R.string.clear_input),
                        modifier = Modifier.clickable {
                            searchFieldText = ""
                        })
                },
                placeholderText = stringResource(R.string.search_text),
                onTextChanged = { newText ->
                    searchFieldText = newText
                    viewModel.handleSearchChange(newText) },
                onFocusChanged = { isFocused -> viewModel.handleSearchTextFocus(isFocused) }
            )

            val state = searchState
            when (state) {
                is SearchScreenState.Loading -> { ProgressBar() }

                is SearchScreenState.Content -> {

                    if (state.tracks.isEmpty()) {
                        val imageResource = getPlaceholderImageResource(false)
                        ErrorMessage(
                            message = stringResource(R.string.nothings_found),
                            iconId = imageResource,
                            topPaddingDp = 110,
                        )
                    }
                    ListOfListTrackInfo(
                        tracks = state.tracks,
                        onTrackClick = {
                            trackId ->
                                viewModel.handleTrackClick(trackId)
                        }
                    )
                }

                is SearchScreenState.Error -> {
                    val imageResource = getPlaceholderImageResource(true)
                    ErrorMessage(
                        message = stringResource(R.string.smth_wrong),
                        iconId = imageResource,
                        topPaddingDp = 110,
                        onUpdateClick = {viewModel.handleSearchChange(searchFieldText)},
                    )
                }

                is SearchScreenState.History -> {
                    TracksHistory(
                        tracks = state.tracks,
                        onClearHistoryClick = { viewModel.clearHistory() },
                        onTrackClick = { viewModel.handleHistoryTrackClick(it) }
                    )
                }

                null -> {}
            }
        }
    }
}

@Composable
private fun getPlaceholderImageResource(connectionFailed: Boolean): Int {
    val isNightMode = isSystemInDarkTheme()
    return if (connectionFailed) {
        if (isNightMode) {
            R.drawable.no_connection_icon_dark
        } else {
            R.drawable.no_connection_icon_light
        }
    } else {
        if (isNightMode) {
            R.drawable.no_results_icon_dark
        } else {
            R.drawable.no_results_icon_light
        }
    }
}

@Composable
fun TracksHistory(
    tracks: List<ListTrackInfo>, onTrackClick: (trackId: Int) -> Unit, onClearHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.search_history_title),
            style = AppTheme.typography.subtitle,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        TrackListHistory(
            tracks = tracks,
            onTrackClick = onTrackClick,
            onButtonClick = onClearHistoryClick
        )
    }
}