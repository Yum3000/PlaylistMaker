package com.example.playlistmaker.media.presentation

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistCreateViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val statePlaylistCreate = MutableLiveData<PlaylistCreateState?>()
    fun observePlaylistCreateState(): LiveData<PlaylistCreateState?> = statePlaylistCreate

    private var coverUri: Uri? = null

    fun handleTitleChange(text: String) {
        val oldState = statePlaylistCreate.value ?: PlaylistCreateState()
        val updateState = oldState.copy(
            title = text, enabledBtn = text.isNotEmpty(),
            dialogNeeded = shouldShowDialog(
                text, oldState.description,
                oldState.filePath?.toUri()
            )
        )
        statePlaylistCreate.postValue(updateState)
    }

    fun handleDescChange(text: String) {
        val oldState = statePlaylistCreate.value ?: PlaylistCreateState()
        val updateState = oldState.copy(
            description = text,
            enabledBtn = oldState.title?.isNotEmpty() == true,
            dialogNeeded = shouldShowDialog(
                oldState.title, text,
                oldState.filePath?.toUri()
            )
        )
        statePlaylistCreate.postValue(updateState)
    }

    fun passArgsUri(uri: Uri) {
        coverUri = uri
        val oldState = statePlaylistCreate.value ?: PlaylistCreateState()
        val updateState = oldState.copy(
            filePath = coverUri.toString(),
            enabledBtn = oldState.title?.isNotEmpty() == true,
            dialogNeeded = shouldShowDialog(
                oldState.title, oldState.description,
                coverUri
            )
        )
        statePlaylistCreate.postValue(updateState)
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            val curState = statePlaylistCreate.value
            val newPlaylist = Playlist(
                null, curState?.title, curState?.description,
                coverUri, emptyList(), 0
            )
            playlistsInteractor.createPlaylist(newPlaylist)
        }
    }

    private fun shouldShowDialog(title: String?, description: String?, uri: Uri?): Boolean {
        return !title.isNullOrEmpty() || !description.isNullOrEmpty() || uri != null
    }
}