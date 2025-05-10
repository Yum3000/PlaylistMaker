package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.SearchTrackInfo

sealed class SearchScreenState {
    data object Loading : SearchScreenState()
    data class History(val tracks: List<SearchTrackInfo>) : SearchScreenState()
    data class Content(val tracks: List<SearchTrackInfo>, val searchQuery: String) : SearchScreenState()
    data class Error(val query: String) : SearchScreenState()
}