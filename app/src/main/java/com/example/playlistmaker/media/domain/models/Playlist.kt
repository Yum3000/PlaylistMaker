package com.example.playlistmaker.media.domain.models

import android.net.Uri

data class Playlist(
    var id: Int = -1,
    val title: String?,
    val description: String?,
    val coverPath: Uri?,
    val tracksIdsList: List<Int>?,
    var tracksCount: Int?
)
