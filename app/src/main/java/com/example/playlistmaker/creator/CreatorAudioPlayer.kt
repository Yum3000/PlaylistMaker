package com.example.playlistmaker.creator

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioPlayerManagerImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerManager
import com.example.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl

object CreatorAudioPlayer {

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    private fun getAudioPlayerManager(): AudioPlayerManager {
        return AudioPlayerManagerImpl(getMediaPlayer())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerManager())
    }
}