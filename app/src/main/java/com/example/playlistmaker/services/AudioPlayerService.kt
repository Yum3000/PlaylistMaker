package com.example.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
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

    private var trackUrl = ""
    private var trackArtist = ""
    private var trackTitle = ""

    private val binder = AudioPlayerServiceBinder()

    private val _playerState = MutableStateFlow<State>(State(PlayerState.DEFAULT, 0))
    val playerState = _playerState.asStateFlow()

    private var timerJob: Job? = null

    data class State(val playerState: PlayerState, val curPos: Int?)

    override fun onBind(intent: Intent?): IBinder? {
        trackUrl = intent?.getStringExtra(INTENT_TRACK_URL_KEY) ?: ""
        trackArtist = intent?.getStringExtra(INTENT_TRACK_ARTIST_KEY) ?: ""
        trackTitle = intent?.getStringExtra(INTENT_TRACK_TITLE_KEY) ?: ""
        initMediaPlayer()
        Log.d("AudioPlayer Service", "OnBind service")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        Log.d("AudioPlayer Service", "onUnbind service")
        return super.onUnbind(intent)
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AudioPlayer Service", "onCreate")
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onDestroy() {
        Log.d("AudioPlayer Service", "onDestroy")
        releasePlayer()
    }

    private fun initMediaPlayer() {
        if (trackUrl.isEmpty()) return

        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            Log.d("AudioPlayer Service", "Media Player prepared")
            _playerState.value = State(PlayerState.PREPARED, 0)
        }
        mediaPlayer?.setOnCompletionListener {
            Log.d("AudioPlayer Service", "Playback completed")
            timerJob?.cancel()
            _playerState.value = State(PlayerState.PREPARED, 0)
            stopForeground()
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
        Log.d("AudioPlayer Service", "release player")
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

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.notification_channel_desc)

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$trackArtist - $trackTitle")
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    override fun startForeground() {
//        if (checkPermissions().not()) {
//            stopSelf()
//            return
//        }
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        const val TIMER_UPDATE_DELAY = 300L

        const val INTENT_TRACK_URL_KEY = "track_url"
        const val INTENT_TRACK_ARTIST_KEY = "track_artist"
        const val INTENT_TRACK_TITLE_KEY = "track_title"

        const val NOTIFICATION_CHANNEL_ID = "notification_channel"
        const val NOTIFICATION_CHANNEL_NAME = "audio_player_service"
        const val SERVICE_NOTIFICATION_ID = 100
    }
}