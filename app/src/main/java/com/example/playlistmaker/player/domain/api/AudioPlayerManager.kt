package com.example.playlistmaker.player.domain.api

interface AudioPlayerManager {
    fun preparePlayer(trackUrl: String, onPrepared: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)
    fun releasePlayer()
}
