package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.services.AudioPlayerService
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
    fun getPlayerState(): StateFlow<AudioPlayerService.State>
    fun startPlayer()
    fun pausePlayer()
    fun startForeground()
    fun stopForeground()
}
