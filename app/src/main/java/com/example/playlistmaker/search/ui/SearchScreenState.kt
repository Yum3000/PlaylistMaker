package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.ListTrackInfo

sealed class SearchScreenState {
    data object Loading : SearchScreenState()
    data class History(val tracks: List<ListTrackInfo>) : SearchScreenState()
    data class Content(val tracks: List<ListTrackInfo>, val searchQuery: String) : SearchScreenState()
    data class Error(val query: String) : SearchScreenState()
}