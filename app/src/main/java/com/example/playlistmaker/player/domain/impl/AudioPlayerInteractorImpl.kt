package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerManager

class AudioPlayerInteractorImpl(private val audioPlayerManager: AudioPlayerManager) :
    AudioPlayerInteractor {

    override fun prepare(trackUrl: String, onPrepared: () -> Unit) {
        audioPlayerManager.preparePlayer( trackUrl, onPrepared)
    }

    override fun startPlayback() {
        audioPlayerManager.startPlayer()
    }

    override fun pausePlayback() {
        audioPlayerManager.pausePlayer()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayerManager.getCurrentPosition()
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        audioPlayerManager.setOnCompletionListener (listener)
    }

    override fun release() {
        audioPlayerManager.releasePlayer()
    }

    override fun isPlaying(): Boolean {
        return audioPlayerManager.isPlaying()
    }
}
