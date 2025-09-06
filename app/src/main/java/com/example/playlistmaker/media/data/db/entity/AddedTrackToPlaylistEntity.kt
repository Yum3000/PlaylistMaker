package com.example.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "added_playlist_tracks_table")
data class AddedTrackToPlaylistEntity(
    @PrimaryKey
    val trackId: Int?,
    val trackName: String?,
    val collectionName: String?,
    val artistName: String?,
    val trackTime: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
)