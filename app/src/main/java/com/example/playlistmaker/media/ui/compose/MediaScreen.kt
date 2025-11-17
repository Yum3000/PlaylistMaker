package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.components.Toolbar
import kotlinx.coroutines.launch

@Composable
fun MediaScreen(
    openAudioPlayerScreen: (trackId: Int) -> Unit,
    openPlaylistScreen: (playlistId: Int) -> Unit,
    openToCreateNewPlaylistScreen: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(topBar = {
        Toolbar(title = stringResource(R.string.media_text))
    }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colors.primaryBackgroundColor)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = AppTheme.colors.primaryBackgroundColor,
                indicator = { position ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(currentTabPosition = position[selectedTabIndex.value])
                            .padding(horizontal = 16.dp),
                        color = AppTheme.colors.primaryTextColor,
                        height = 2.dp,
                    )
                },
                divider = {
                    TabRowDefaults.Divider(
                        color = AppTheme.colors.primaryBackgroundColor
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex.value == MediaTabs.Favourites.ordinal,
                    text = {
                        Text(
                            text = stringResource(R.string.favourites_tracks),
                            style = AppTheme.typography.body2
                        )
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(MediaTabs.Favourites.ordinal)
                        }
                    }
                )

                Tab(
                    selected = selectedTabIndex.value == MediaTabs.Playlists.ordinal,
                    text = {
                        Text(
                            text = stringResource(R.string.playlists),
                            style = AppTheme.typography.body2
                        )
                    },
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(MediaTabs.Playlists.ordinal)
                        }
                    }
                )

            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    MediaTabs.Favourites.ordinal -> {
                        FavouritesScreen(
                             openAudioPlayerScreen
                        )
                    }

                    MediaTabs.Playlists.ordinal -> {
                        PlaylistsScreen(
                            openPlaylistScreen,
                            openToCreateNewPlaylistScreen
                        )
                    }
                }
            }
        }
    }
}