package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.FavTracksDao
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity

@Database(version = 1, entities = [FavouriteTrackEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun favTracksDao(): FavTracksDao
}