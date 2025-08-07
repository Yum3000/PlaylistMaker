package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private var timerJob: Job? = null

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
        }
    }

    fun getPlayerStateLiveData(): LiveData<PlayerScreenState> = playerStateLiveData

    fun getPlayerErrorToast(): SingleLiveEvent<Unit> = playerErrorToast

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
        stopTimer()
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
        startTimer()
    }

    fun pause() {
        playerInteractor.pausePlayback()
        stopTimer()
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

            PlayerState.PAUSED, PlayerState.PREPARED -> {
                play()
            }

            else -> {
                playerErrorToast.postValue(Unit)
            }
        }
    }

    private fun startTimer(){
        timerJob = viewModelScope.launch {
            while(playerInteractor.isPlaying()) {
                delay(300L)
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
            }
        }
    }

    private fun stopTimer(){
        timerJob?.cancel()
        timerJob = null
    }
}