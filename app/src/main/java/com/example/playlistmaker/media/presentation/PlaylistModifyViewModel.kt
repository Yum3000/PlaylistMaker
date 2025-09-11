package com.example.playlistmaker.media.presentation

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistModifyViewModel(
    private val playlistId: Int,
    playlistsInteractor: PlaylistsInteractor): PlaylistCreateViewModel(
    playlistsInteractor
) {

    private val initialPlaylistState = MutableLiveData<Playlist>()
    fun observeInitPlaylistState(): LiveData<Playlist> = initialPlaylistState

    init {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            statePlaylistCreate.postValue(
                PlaylistCreateState(
                    playlist?.title,
                    playlist?.description,
                    playlist?.coverPath.toString(),
                    enabledBtn = true,
                    dialogNeeded = false
                )
            )
            if (playlist != null) initialPlaylistState.postValue(playlist)
        }
    }

    fun saveUpdates(playlistId: Int) {

        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            val curState = statePlaylistCreate.value
            if (curState != null) {
                val updatedPlaylist = Playlist(
                    id = playlistId,
                    title = curState.title,
                    description = curState.description,
                    coverPath = curState.filePath?.toUri(),
                    tracksIdsList = playlist?.tracksIdsList ?: emptyList(),
                    tracksCount = playlist?.tracksCount ?: 0
                )

                playlistsInteractor.updatePlaylist(updatedPlaylist)

                statePlaylistCreate.postValue(
                    PlaylistCreateState(
                        title = updatedPlaylist.title,
                        description = updatedPlaylist.description,
                        filePath = updatedPlaylist.coverPath.toString(),
                        enabledBtn = true,
                        dialogNeeded = false
                    )
                )
            }
        }
    }
}