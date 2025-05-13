package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.creator.CreatorAudioPlayer
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackId: Int,
    private val playerInteractor: AudioPlayerInteractor,
    historyInteractor: TracksHistoryInteractor
) : ViewModel() {

    private var playerStateLiveData = MutableLiveData<PlayerScreenState>()

    private var playerErrorToast = SingleLiveEvent<Unit>()

    private val playerTrackInfo: PlayerTrackInfo

    private var handler = Handler(Looper.getMainLooper())

    init {
        val track: Track? = historyInteractor.getHistory().firstOrNull { it.trackId == trackId }
        playerTrackInfo = trackToPlayerTrackInfo(track)
        playerStateLiveData.value = PlayerScreenState(
            playerState = PlayerState.DEFAULT,
            trackInfo = playerTrackInfo,
        )

        val preview = playerTrackInfo.previewUrl
        if (!preview.isNullOrEmpty()) {
            playerInteractor.prepare(preview) {
                playerStateLiveData.value = PlayerScreenState(
                    playerState = PlayerState.PREPARED,
                    trackInfo = playerTrackInfo,
                )
            }
        }

        playerInteractor.setOnCompletionListener {
            playerStateLiveData.value = PlayerScreenState(
                playerState = PlayerState.PREPARED,
                trackInfo = playerTrackInfo,
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

    fun getPlayerErrorToast(): SingleLiveEvent<Unit> = playerErrorToast

    companion object {
        fun getViewModelFactory(trackId: Int): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(
                        trackId,
                        CreatorAudioPlayer.provideAudioPlayerInteractor(),
                        CreatorHistory.provideTracksHistoryInteractor()
                    )
                }
            }
    }

    private fun trackToPlayerTrackInfo(track: Track?): PlayerTrackInfo {
        return PlayerTrackInfo(
            track?.trackId ?: -1,
            track?.trackName ?: "",
            track?.collectionName,
            track?.artistName ?: "",
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
                playerErrorToast.postValue(Unit)
            }
        }
    }
}