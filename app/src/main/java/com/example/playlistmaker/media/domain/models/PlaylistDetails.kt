package com.example.playlistmaker.media.domain.models

import com.example.playlistmaker.search.domain.models.Track

data class PlaylistDetails(
    val title: String,
    val description: String,
    val coverPath: String,
    val tracksList: List<Track>,
    val tracksCount: String,
    val tracksDuration: String
)
