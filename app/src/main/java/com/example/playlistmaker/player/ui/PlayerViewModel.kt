package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.media.domain.db.FavTracksInteractor
import com.example.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackId: Int,
    private val playerInteractor: AudioPlayerInteractor,
    historyInteractor: TracksHistoryInteractor,
    private val favTracksInteractor: FavTracksInteractor
) : ViewModel() {

    private var playerStateLiveData = MutableLiveData<PlayerScreenState>()

    private var playerErrorToast = SingleLiveEvent<Unit>()

    private lateinit var playerTrackInfo: PlayerTrackInfo

    private var timerJob: Job? = null

    private var currentTrack: Track? = null

    init {
        viewModelScope.launch {
            val track: Track? = historyInteractor.getHistory().firstOrNull { it.trackId == trackId }
            currentTrack = track
            playerTrackInfo = trackToPlayerTrackInfo(track)

            checkIsFavourite(currentTrack?.trackId)

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
                stopTimer()
                playerStateLiveData.value = PlayerScreenState(
                    playerState = PlayerState.PREPARED,
                    trackInfo = playerTrackInfo,
                    curPosition = null
                )
            }
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
            track?.previewUrl ?: "",
            track?.isFavourite ?: false
        )
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        stopTimer()
    }

    private fun play() {
        val oldState = playerStateLiveData.value
        if (oldState == null) return
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
        if (oldState == null) return
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

    private fun startTimer(){
        timerJob?.cancel()
        timerJob = viewModelScope.launch (Dispatchers.IO) {
            while(playerInteractor.isPlaying()) {
                delay(TIMER_UPDATE_DELAY)
                val curPos = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(playerInteractor.getCurrentPosition())

                val oldState = playerStateLiveData.value
                if (oldState != null) {
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
    }

    private fun stopTimer(){
        timerJob?.cancel()
        timerJob = null
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch (Dispatchers.IO) {
                if (!track.isFavourite) {
                    favTracksInteractor.addFavTrack(track)
                    track.isFavourite = true
                } else {
                    favTracksInteractor.removeFavTrack(track)
                    track.isFavourite = false
                }
                updatePlayerScreenState()
            }
        }
    }

    private fun updatePlayerScreenState(){
        val oldState = playerStateLiveData.value
        if (oldState != null) {
            val updatedTrackInfo = oldState.trackInfo.copy(
                isFavourite = currentTrack?.isFavourite ?: false
            )
            playerStateLiveData.postValue(
                PlayerScreenState(
                    playerState = oldState.playerState,
                    trackInfo = updatedTrackInfo,
                    curPosition = oldState.curPosition
                )
            )
        }
    }

    private suspend fun checkIsFavourite(trackId: Int?){
        favTracksInteractor.getFavTracksId().collect{ ids ->
            if (ids.contains(trackId)){
                currentTrack?.isFavourite = true
            } else {
                currentTrack?.isFavourite = false
            }
            updatePlayerScreenState()
        }
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}