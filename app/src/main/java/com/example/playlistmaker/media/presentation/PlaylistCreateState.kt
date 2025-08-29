package com.example.playlistmaker.media.presentation

data class PlaylistCreateState(
    val title: String?,
    val description: String?,
    val filePath: String?,
    val enabledBtn: Boolean = false
)
