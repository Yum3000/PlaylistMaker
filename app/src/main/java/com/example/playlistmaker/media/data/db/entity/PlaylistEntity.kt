package com.example.playlistmaker.media.data.db.entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String?,
    val description: String?,
    val coverPath: String?,
    val tracksIdsList: String?,
    val tracksCount: Int?
    )
