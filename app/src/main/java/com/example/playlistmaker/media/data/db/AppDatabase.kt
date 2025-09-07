package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.AddToPlaylistDao
import com.example.playlistmaker.media.data.db.dao.FavTracksDao
import com.example.playlistmaker.media.data.db.dao.PlaylistsDao
import com.example.playlistmaker.media.data.db.entity.AddedTrackToPlaylistEntity
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity

@Database(version = 1, entities =
    [FavouriteTrackEntity::class, PlaylistEntity::class, AddedTrackToPlaylistEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun favTracksDao(): FavTracksDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun addToPlaylistDao(): AddToPlaylistDao
}