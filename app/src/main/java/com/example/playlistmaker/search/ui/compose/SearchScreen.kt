package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.CustomTextField
import com.example.playlistmaker.ProgressBar
import com.example.playlistmaker.R
import com.example.playlistmaker.Toolbar
import com.example.playlistmaker.TrackList
import com.example.playlistmaker.search.ui.SearchScreenState
import com.example.playlistmaker.search.ui.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {
    var searchText by remember { mutableStateOf("") }


    val searchState by viewModel.getSearchStateLiveData().observeAsState()

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
            CustomTextField(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                    //.focusRequester(focusRequester),
                text = "",//searchState.searchText,
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
//                            viewModel.clearSearchRequest()
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
//                    SearchScreenError(
//                        errorType = (screenState as SearchScreenState.Error).errorType,
//                        onUpdateClick = viewModel::searchTrack
//                    )
                }

                is SearchScreenState.History -> {
//                    SearchHistoryBlock(
//                        tracks = (screenState as SearchScreenState.History).tracks,
//                        onTrackClick = viewModel::onTrackClicked,
//                        onClearHistoryClick = viewModel::clearHistory
//                    )
                }

                null -> {}
            }
        }

    }
}