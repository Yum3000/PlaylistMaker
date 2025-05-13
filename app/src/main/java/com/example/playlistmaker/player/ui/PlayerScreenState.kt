package com.example.playlistmaker.player.ui

import com.example.playlistmaker.player.domain.models.PlayerTrackInfo

data class PlayerScreenState(
    val playerState: PlayerState = PlayerState.DEFAULT,
    val trackInfo: PlayerTrackInfo,
    val curPosition: String = "00:00"
)