package com.example.playlistmaker.domain.api

interface AudioPlayerInteractor {
    fun prepare(trackUrl: String, onPrepared: () -> Unit)
    fun startPlayback()
    fun pausePlayback()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
    fun release()
}