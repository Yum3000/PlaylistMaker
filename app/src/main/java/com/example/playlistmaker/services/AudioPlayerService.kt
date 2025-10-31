package com.example.playlistmaker.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.playlistmaker.player.domain.api.AudioPlayerManager
import com.example.playlistmaker.player.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioPlayerService: Service(), AudioPlayerManager {

    private var mediaPlayer: MediaPlayer? = null

    private var songUrl = ""

    private val binder = AudioPlayerServiceBinder()

    private val _playerState = MutableStateFlow<State>(State(PlayerState.DEFAULT, 0))
    val playerState = _playerState.asStateFlow()

    private var timerJob: Job? = null

    data class State(val playerState: PlayerState, val curPos: Int?)

    override fun onBind(intent: Intent?): IBinder? {
        songUrl = intent?.getStringExtra("track_url") ?: ""
        initMediaPlayer()

//        ServiceCompat.startForeground(
//            this,
//            SERVICE_NOTIFICATION_ID,
//            createServiceNotification(),
//            getForegroundServiceTypeConstant()
//        )
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AudioPlayer Service", "onCreate")
        mediaPlayer = MediaPlayer()
    }

    override fun onDestroy() {
        Log.d("AudioPlayer Service", "onDestroy")
        releasePlayer()
    }

    private fun initMediaPlayer() {
        if (songUrl.isEmpty()) return

        mediaPlayer?.setDataSource(songUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            Log.d("AudioPlayer Service", "Media Player prepared")
            _playerState.value = State(PlayerState.PREPARED, 0)
        }
        mediaPlayer?.setOnCompletionListener {
            Log.d("AudioPlayer Service", "Playback completed")
            _playerState.value = State(PlayerState.PREPARED, 0)
        }
    }

    // уже есть доступ через asStateFlow() ??
    override fun fetchPlayerState(): StateFlow<State> {
        return playerState
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = State(PlayerState.PLAYING, mediaPlayer?.currentPosition)
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = State(PlayerState.PAUSED, mediaPlayer?.currentPosition)
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        stopTimer()
        _playerState.value = State(PlayerState.DEFAULT, null)
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(TIMER_UPDATE_DELAY)

                val curPos = mediaPlayer?.currentPosition ?: 0
                _playerState.value = playerState.value.copy(curPos = curPos)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}