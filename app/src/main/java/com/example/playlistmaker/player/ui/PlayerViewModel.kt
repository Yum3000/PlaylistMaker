package com.example.playlistmaker.player.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.CreatorAudioPlayer
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val application: Application,
    private val trackId: Int,
    private val playerInteractor: AudioPlayerInteractor,
    historyInteractor: TracksHistoryInteractor
) : AndroidViewModel(application) {

    private var playerStateLiveData = MutableLiveData<PlayerScreenState>()

    private val playerTrackInfo: PlayerTrackInfo

    private var handler = Handler(Looper.getMainLooper())

    init {
        val track: Track? = historyInteractor.getHistory().firstOrNull { it.trackId == trackId }
        playerTrackInfo = trackToPlayerTrackInfo(track)
        playerStateLiveData.value = PlayerScreenState(
            playerState = PlayerState.DEFAULT,
            trackInfo = playerTrackInfo,
            curPosition = DEFAULT_CUR_POSITION
        )

        val preview = playerTrackInfo.previewUrl
        if (!preview.isNullOrEmpty()) {
            playerInteractor.prepare(preview) {
                playerStateLiveData.value = PlayerScreenState(
                    playerState = PlayerState.PREPARED,
                    trackInfo = playerTrackInfo,
                    curPosition = DEFAULT_CUR_POSITION
                )
            }
        }

        playerInteractor.setOnCompletionListener {
            playerStateLiveData.value = PlayerScreenState(
                playerState = PlayerState.PREPARED,
                trackInfo = playerTrackInfo,
                curPosition = DEFAULT_CUR_POSITION
            )
            handler.removeCallbacks(updateTrackTimeRunnable)
        }
    }

    private val updateTrackTimeRunnable = object : Runnable {
        override fun run() {
            val curPos = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(playerInteractor.getCurrentPosition())


            val oldState = playerStateLiveData.value
            playerStateLiveData.postValue(
                PlayerScreenState(
                    playerState = oldState.playerState,
                    trackInfo = oldState.trackInfo,
                    curPosition = curPos
                )
            )
            handler.postDelayed(this, 500L)
        }
    }

    fun getPlayerStateLiveData(): LiveData<PlayerScreenState> = playerStateLiveData

    companion object {
        fun getViewModelFactory(application: Application, trackId: Int): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(
                        application,
                        trackId,
                        CreatorAudioPlayer.provideAudioPlayerInteractor(),
                        CreatorHistory.provideTracksHistoryInteractor()
                    )
                }
            }

        const val DEFAULT_CUR_POSITION = "00:00"
    }

    private fun trackToPlayerTrackInfo(track: Track?): PlayerTrackInfo {
        return PlayerTrackInfo(
            track?.trackId ?: -1,
            track?.artistName ?: "",
            track?.collectionName,
            track?.trackName ?: "",
            track?.trackTimeMillis ?: "",
            track?.getTrackReleaseDate() ?: "",
            track?.primaryGenreName ?: "",
            track?.country ?: "",
            track?.getCoverArtwork() ?: "",
            track?.previewUrl ?: ""
        )
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        handler.removeCallbacks(updateTrackTimeRunnable)
    }

    private fun play() {
        val oldState = playerStateLiveData.value
        playerStateLiveData.postValue(
            PlayerScreenState(
                playerState = PlayerState.PLAYING,
                trackInfo = oldState.trackInfo,
                curPosition = oldState.curPosition
            )
        )
        playerInteractor.startPlayback()
        handler.post(updateTrackTimeRunnable)
    }

    fun pause() {
        playerInteractor.pausePlayback()
        handler.removeCallbacks(updateTrackTimeRunnable)
        val oldState = playerStateLiveData.value
        playerStateLiveData.postValue(
            PlayerScreenState(
                playerState = PlayerState.PAUSED,
                trackInfo = oldState.trackInfo,
                curPosition = oldState.curPosition
            )
        )
    }

    private fun showToast() {
        Toast.makeText(
            application,
            R.string.playing_error,
            Toast.LENGTH_LONG
        ).show()
    }

    fun handlePlayBtnClick() {
        when (playerStateLiveData.value?.playerState) {
            PlayerState.PLAYING -> {
                pause()
            }

            PlayerState.PAUSED -> {
                play()
            }

            PlayerState.PREPARED -> {
                play()
            }

            else -> {
                showToast()
            }
        }
    }
}