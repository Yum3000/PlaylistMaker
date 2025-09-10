package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.domain.models.PlaylistDetails
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.utils.ResourceProvider
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistId: Int,
    private val resourceProvider: ResourceProvider,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private lateinit var curPlaylist: Playlist
    private lateinit var tracks: List<Track>

    private val playlistState = MutableLiveData<PlaylistState>()
    fun observePlaylistState(): LiveData<PlaylistState> = playlistState

    private val trackToOpenPlayer = SingleLiveEvent<ListTrackInfo>()
    fun getTrackToOpenPlayer(): LiveData<ListTrackInfo> = trackToOpenPlayer

    private val toastMsgLiveData = SingleLiveEvent<String>()
    fun observeToastMsgLiveData(): LiveData<String> = toastMsgLiveData

    private val playlistToDelete = SingleLiveEvent<Boolean>()
    fun observePlaylistToDelete(): LiveData<Boolean> = playlistToDelete

    init {
        updatePlaylistDetails()
    }

    fun updatePlaylistDetails() {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId) ?: return@launch
            curPlaylist = playlist

            val trackCountStr = getCountString(curPlaylist.tracksCount, resourceProvider, false)

            val tracksIdsList = curPlaylist.tracksIdsList

            if (tracksIdsList == null) return@launch

            playlistsInteractor.getTracksByIds(tracksIdsList).collect { trackList ->

                tracks = trackList

                val duration = getTracksDuration(tracks).toInt()
                val minutesCountStr = getCountString(duration, resourceProvider, true)

                playlistState.postValue(
                    PlaylistState.Content(
                        PlaylistDetails(
                            title = curPlaylist.title ?: "",
                            description = curPlaylist.description ?: "",
                            coverPath = curPlaylist.coverPath.toString(),
                            tracksList = tracks,
                            tracksCount = trackCountStr,
                            tracksDuration = minutesCountStr
                        ),
                    )
                )
            }

        }
    }

    private fun getTracksDuration(tracks: List<Track>): String {
        val tracksDuration = tracks.map { mmssToMillis(it.trackTimeMillis) }
        val sumMillis = tracksDuration.sum()
        return SimpleDateFormat(FULL_LENGTH_FORMAT, Locale.getDefault()).format(sumMillis)
    }

    private fun mmssToMillis(input: String?): Int {
        if (input == null) return 0
        val parts = input.split(":")
        if (parts.size != 2) return 0
        val min = parts[0].toIntOrNull() ?: 0
        val sec = parts[1].toIntOrNull() ?: 0
        return (min * 60 + sec) * 1000
    }

    private val handleTrackClickDebounced = debounce<ListTrackInfo>(
        CLICK_TRACK_DEBOUNCE_DELAY, viewModelScope, false
    ) { track ->
        trackToOpenPlayer.postValue(track)
    }

    fun handleTrackClick(track: ListTrackInfo) {
        handleTrackClickDebounced(track)
    }

    fun deleteTrack(trackId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrack(trackId, curPlaylist)
            val updatedTrackCount = playlistsInteractor.getTrackCount(curPlaylist.id)
            curPlaylist.tracksCount = updatedTrackCount

            curPlaylist = playlistsInteractor.getPlaylistById(curPlaylist.id) ?: curPlaylist
            val ids = curPlaylist.tracksIdsList ?: emptyList()

            playlistsInteractor.getTracksByIds(ids).collect { tracks ->

                val trackCountStr = getCountString(updatedTrackCount, resourceProvider, false)
                val minutesCountStr =
                    getCountString(getTracksDuration(tracks).toInt(), resourceProvider, true)

                updatePlaylistState(
                    tracks,
                    trackCountStr,
                    minutesCountStr
                )
            }

        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(curPlaylist.id)
            playlistToDelete.postValue(true)
        }
    }

    private fun updatePlaylistState(
        newTracks: List<Track>, newTracksCount: String, newTracksDuration: String
    ) {
        val oldState = playlistState.value
        if (oldState is PlaylistState.Content) {
            val oldDetails = oldState.playlistDetails

            val updatedDetails = PlaylistDetails(
                title = oldDetails.title,
                description = oldDetails.description,
                coverPath = oldDetails.coverPath,
                tracksList = newTracks,
                tracksCount = newTracksCount,
                tracksDuration = newTracksDuration
            )

            val updatedPlaylist = PlaylistState.Content(updatedDetails)

            playlistState.postValue(updatedPlaylist)
        }
    }

    fun sharePlaylist() {
        if (curPlaylist.tracksIdsList?.isEmpty() == true) toastMsgLiveData.value =
            resourceProvider.getString(R.string.empty_playlist_to_share)
        else {
            sharingInteractor.shareLink(getMsgToShare())
        }
    }

    private fun getMsgToShare(): String {
        val messageBuilder = StringBuilder()
        messageBuilder.append("${curPlaylist.title}\n")
        if (!curPlaylist.description.isNullOrBlank()) messageBuilder.append("${curPlaylist.description}\n")

        val count = curPlaylist.tracksCount ?: 0

        val trackCountStr = getCountString(count, resourceProvider, false)

        messageBuilder.append("$trackCountStr\n")

        for (i in 0 until tracks.size) {
            messageBuilder.append(
                "${i + 1}. ${tracks[i].artistName} - " +
                        "${tracks[i].trackName} " +
                        "(${tracks[i].trackTimeMillis})\n"
            )
        }

        return messageBuilder.toString()
    }

    private fun getCountString(
        count: Int?, resourceProvider: ResourceProvider, isTime: Boolean
    ): String {
        val trackCount = count ?: 0
        val resourceId = when {
            (trackCount % 10 == 1 && trackCount % 100 != 11) ->
                if (isTime) R.string.minute_count_single else R.string.tracks_count_single

            (trackCount % 10 in 2..4 && trackCount % 100 !in 12..14) ->
                if (isTime) R.string.minutes_count_few else R.string.tracks_count_few

            else -> if (isTime) R.string.minutes_count_many else R.string.tracks_count_many
        }
        return resourceProvider.getString(resourceId, trackCount)
    }

    companion object {
        private const val FULL_LENGTH_FORMAT = "mm"
        const val CLICK_TRACK_DEBOUNCE_DELAY = 1000L
    }
}