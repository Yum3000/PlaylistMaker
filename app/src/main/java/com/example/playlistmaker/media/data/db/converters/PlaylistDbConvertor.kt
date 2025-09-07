package com.example.playlistmaker.media.data.db.converters

import android.util.Log
import androidx.core.net.toUri
import com.example.playlistmaker.media.data.db.entity.AddedTrackToPlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        Log.d("PlaylistDBConvertor", "COUNT = ${playlist.tracksIdsList?.size}")
        return PlaylistEntity(
            playlist.id, playlist.title, playlist.description,
            playlist.coverPath?.toString(),
            serializeTracksIdsList(playlist.tracksIdsList),
            playlist.tracksIdsList?.size
        )

    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id, playlist.title, playlist.description,
            playlist.coverPath?.let{ it.toUri()},
            deserializeTracksIdsList(playlist.tracksIdsList),
            playlist.tracksCount
        )
    }

    fun serializeTracksIdsList(tracksIdsList: List<Int>?): String {
        return Gson().toJson(tracksIdsList)
    }

    fun deserializeTracksIdsList(tracksIdsList: String?): List<Int>? {
        return Gson().fromJson(
            tracksIdsList, object: TypeToken<List<Int?>>() {}.type
        )
    }

    fun trackToAddedTrack(track: Track): AddedTrackToPlaylistEntity {
        return AddedTrackToPlaylistEntity(
            track.trackId, track.trackName, track.collectionName,
            track.artistName, track.trackTimeMillis, track.releaseDate,
            track.primaryGenreName, track.country, track.artworkUrl100,
            track.previewUrl
        )
    }
}