package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.AudioPlayerManager

class AudioPlayerManagerImpl(private var mediaPlayer: MediaPlayer) : AudioPlayerManager {

    private var onCompleteListener: (() -> Unit)? = null

    override fun preparePlayer(trackUrl: String, onPrepared: () -> Unit) {
        mediaPlayer.apply {
            setDataSource(trackUrl)
            prepareAsync()
            setOnPreparedListener {
                onPrepared()
            }
            setOnCompletionListener {
                onCompleteListener?.invoke()
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition ?: 0
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying ?: false
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompleteListener = listener
    }

    override fun releasePlayer() {
        mediaPlayer.reset()
    }
}
