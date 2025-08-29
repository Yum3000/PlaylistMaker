package com.example.playlistmaker.media.domain.models

import android.net.Uri

data class Playlist(
    val id: Int? = null,
    val title: String?,
    val description: String?,
    val coverPath: Uri?,
    val tracksIdsList: List<Int>?,
    val tracksCount: Int?
)
