package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.media.domain.db.FavTracksInteractor
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.api.AudioPlayerManager
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val trackId: Int,
    historyInteractor: TracksHistoryInteractor,
    private val favTracksInteractor: FavTracksInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var playerStateLiveData = MutableLiveData<PlayerScreenState>()

    private var playerErrorToast = SingleLiveEvent<Unit>()

    private lateinit var playerTrackInfo: PlayerTrackInfo

    private var currentTrack: Track? = null

    private val playlistBottomState = MutableLiveData<PlaylistBottomState>()
    fun observeBottomState(): LiveData<PlaylistBottomState> = playlistBottomState

    val listPlaylists: MutableList<Playlist> = mutableListOf()

    private val addTrackPlaylistStatus = MutableLiveData<AddTrackStatus>()
    fun observeAddTrackStatus(): LiveData<AddTrackStatus> = addTrackPlaylistStatus

    private var audioPlayerManager: AudioPlayerManager? = null
    private var audioPlayerManagerJob: Job? = null

    init {
        viewModelScope.launch {
            val track: Track? = historyInteractor.getHistory().firstOrNull { it.trackId == trackId }
            currentTrack = track
            playerTrackInfo = trackToPlayerTrackInfo(track)

            checkIsFavourite(currentTrack?.trackId)

            playerStateLiveData.value = PlayerScreenState(
                playerState = PlayerState.DEFAULT,
                trackInfo = playerTrackInfo,
                curPosition = TIMER_DEFAULT_POS
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
            track?.previewUrl ?: "",
            track?.isFavourite ?: false
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopForeground()
        audioPlayerManager = null
    }

    fun handlePlayBtnClick() {
        when (playerStateLiveData.value?.playerState) {
            PlayerState.PLAYING -> {
                audioPlayerManager?.pausePlayer()
            }

            PlayerState.PAUSED -> {
                audioPlayerManager?.startPlayer()
            }

            PlayerState.PREPARED -> {
                audioPlayerManager?.startPlayer()
            }

            else -> {
                playerErrorToast.postValue(Unit)
            }
        }
    }

    fun onFavoriteClicked() {
        currentTrack?.let { track ->
            viewModelScope.launch(Dispatchers.IO) {
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

    private fun updatePlayerScreenState() {
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

    private suspend fun checkIsFavourite(trackId: Int?) {
        favTracksInteractor.getFavTracksId().collect { ids ->
            currentTrack?.isFavourite = ids.contains(trackId)
            updatePlayerScreenState()
        }
    }

    fun handleDestroyView() {
        addTrackPlaylistStatus.value = AddTrackStatus.Default
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                listPlaylists.clear()
                listPlaylists.addAll(playlists)
                playlistBottomState.postValue(PlaylistBottomState.Content(playlists))
            }
        }
    }

    fun handleAddToPlaylistClick(playlistId: Int, playlistName: String?) {

        val playlist = listPlaylists.find { it.id == playlistId } ?: return
        val currentTrack = currentTrack ?: return

        if (playlist.tracksIdsList?.contains(trackId) == true) {
            addTrackPlaylistStatus.postValue(AddTrackStatus.Exists(playlistName))
            return
        }

        viewModelScope.launch {
            playlistsInteractor.addTrackToPlaylist(currentTrack, playlist)

            val updatedTrackCount = playlistsInteractor.getTrackCount(playlist.id)
            playlist.tracksCount = updatedTrackCount
            addTrackPlaylistStatus.postValue(AddTrackStatus.Added(playlistName))
        }
    }

    fun setAudioPlayerManager(audioPlayerManager: AudioPlayerManager) {
        this.audioPlayerManager = audioPlayerManager

        audioPlayerManagerJob = viewModelScope.launch {
            audioPlayerManager.getPlayerState().collect { state ->
                val oldState = playerStateLiveData.value
                if (oldState == null) {
                    return@collect
                }

                if ((state.playerState == PlayerState.PLAYING || state.playerState == PlayerState.PAUSED)) {
                    playerStateLiveData.postValue(
                        PlayerScreenState(
                            playerState = state.playerState,
                            trackInfo = oldState.trackInfo,
                            curPosition = currentPlayerPositionToStr(state.curPos)
                        )
                    )
                } else {
                    playerStateLiveData.postValue(
                        PlayerScreenState(
                            playerState = state.playerState,
                            trackInfo = oldState.trackInfo,
                            curPosition = currentPlayerPositionToStr(0)
                        )
                    )
                }
            }
        }
    }

    fun removeAudioPlayerManager() {
        audioPlayerManager = null
    }

    private fun currentPlayerPositionToStr(position: Int?): String {
        return SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(
            position
        ) ?: TIMER_DEFAULT_POS
    }

    fun startForeground() {
        if (playerStateLiveData.value?.playerState == PlayerState.PLAYING) {
            audioPlayerManager?.startForeground()
        }
    }

    fun stopForeground() {
        if (playerStateLiveData.value?.playerState == PlayerState.PLAYING) {
            audioPlayerManager?.stopForeground()
        }
    }

    companion object {
        const val TIMER_DEFAULT_POS = "00:00"
        const val TIME_PATTERN = "mm:ss"
    }
}