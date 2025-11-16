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
import com.example.playlistmaker.Button
import com.example.playlistmaker.CustomTextField
import com.example.playlistmaker.EmptyMessage
import com.example.playlistmaker.ProgressBar
import com.example.playlistmaker.R
import com.example.playlistmaker.Toolbar
import com.example.playlistmaker.TrackList
import com.example.playlistmaker.TrackListHistory
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {
    var searchText by remember { mutableStateOf("") }

    val searchState by viewModel.getSearchStateLiveData().observeAsState()


//    LifecycleStartEffect(Unit) {
//        viewModel.updateSearchResults()
//        viewModel.updateHistory()
//        onStopOrDispose { }
//    }

    Scaffold(topBar = {
        Toolbar(title = stringResource(R.string.search_text))
    }) { innerPadding ->
        Column(
            modifier = Modifier
                //.consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .background(color = AppTheme.colors.primaryBackgroundColor)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentSearchText = when (searchState) {
                is SearchScreenState.Content -> (searchState as SearchScreenState.Content).searchQuery
                else -> ""
            }

            CustomTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    //.focusRequester(focusRequester),
                text = currentSearchText,
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
//                            focusManager.clearFocus()
                            viewModel.handleSearchChange("")
                        })
                },
                placeholderText = stringResource(R.string.search_text),
                onTextChanged = { newText -> viewModel.handleSearchChange(newText) },
                onFocusChanged = { isFocused -> viewModel.handleSearchTextFocus(isFocused) }
            )

            when (searchState) {
                is SearchScreenState.Loading -> { ProgressBar() }

                is SearchScreenState.Content -> {
//                    TrackList(
//                        tracks = (searchState as SearchScreenState.Content).tracks,
//                        onTrackClick = viewModel::onTrackClicked
//                    )
                }

                is SearchScreenState.Error -> {
                    ErrorMessage(
                        connectionFailed = true,
                        onUpdateClick = {viewModel.handleSearchChange(searchText)}
                    )
                }

                is SearchScreenState.History -> {
                    TracksHistory(
                        tracks = mutableListOf(),
                                //(searchState as SearchScreenState.History).tracks,
                        onClearHistoryClick = { viewModel.clearHistory() },
                        onTrackClick = { viewModel.handleTrackClick(0) } //!!!!
                    )
                }

                null -> {}
            }
        }

    }
}

@Composable
fun ErrorMessage(connectionFailed: Boolean, onUpdateClick: () -> Unit) {
    val topPaddingDp = 110

    val imageResource = getPlaceholderImageResource(connectionFailed)

    if (connectionFailed) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyMessage(
                message = stringResource(R.string.smth_wrong),
                iconId = imageResource,
                topPaddingDp = topPaddingDp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(stringResource(R.string.refresh), onClick = onUpdateClick)
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
    tracks: List<Track>, onTrackClick: (Track) -> Unit, onClearHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(42.dp))

        Text(
            text = stringResource(R.string.search_history_title),
            //style = AppTheme.typography.h2,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        TrackListHistory(
            tracks = tracks,
            onTrackClick = onTrackClick,
            onButtonClick = onClearHistoryClick
        )
    }
}