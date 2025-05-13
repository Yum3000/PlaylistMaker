package com.example.playlistmaker.search.domain.models

data class SearchTrackInfo(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String,
)