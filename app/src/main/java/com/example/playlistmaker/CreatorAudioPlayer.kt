package com.example.playlistmaker

import android.media.MediaPlayer
import com.example.playlistmaker.data.AudioPlayerManagerImpl
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.AudioPlayerManager
import com.example.playlistmaker.domain.impl.AudioPlayerInteractorImpl

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