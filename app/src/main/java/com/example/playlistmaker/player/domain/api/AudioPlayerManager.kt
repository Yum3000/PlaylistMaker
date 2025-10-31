package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.services.AudioPlayerService
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
//    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit)
//    fun startPlayer()
//    fun pausePlayer()
//    fun getCurrentPosition(): Int
//    fun isPlaying(): Boolean
//    fun setOnCompletionListener(listener: () -> Unit)
//    fun releasePlayer()

    fun fetchPlayerState(): StateFlow<AudioPlayerService.State>
    fun startPlayer()
    fun pausePlayer()

}
