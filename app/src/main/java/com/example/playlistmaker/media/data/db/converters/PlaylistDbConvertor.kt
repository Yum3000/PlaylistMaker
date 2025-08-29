package com.example.playlistmaker.media.data.db.converters

import androidx.core.net.toUri
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id, playlist.title, playlist.description,
            playlist.coverPath?.toString(),
            serializeTracksIdsList(playlist.tracksIdsList),
            playlist.tracksCount
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

    private fun serializeTracksIdsList(tracksIdsList: List<Int>?): String {
        return Gson().toJson(tracksIdsList)
    }

    private fun deserializeTracksIdsList(tracksIdsList: String?): List<Int>? {
        return Gson().fromJson(
            tracksIdsList, object: TypeToken<List<Int>>() {}.type
        )
    }
}