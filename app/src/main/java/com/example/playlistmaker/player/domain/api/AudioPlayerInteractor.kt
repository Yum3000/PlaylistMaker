package com.example.playlistmaker.player.domain.api

interface AudioPlayerInteractor {
    fun prepare(trackUrl: String, onPrepared: () -> Unit)
    fun startPlayback()
    fun pausePlayback()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
    fun release()
    fun isPlaying(): Boolean
}