package com.example.playlistmaker.media.presentation

data class PlaylistCreateState(
    val title: String? = null,
    val description: String? = null,
    val filePath: String? = null,
    val enabledBtn: Boolean = false,
    val dialogNeeded: Boolean = false
)
