package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity

@Dao
interface FavTracksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavourites(track: FavouriteTrackEntity)

    @Delete(FavouriteTrackEntity::class)
    suspend fun removeFromFavourites(track: FavouriteTrackEntity)

    @Query("SELECT * FROM fav_tracks_table ORDER BY createdTime DESC")
    suspend fun getFavTracks(): List<FavouriteTrackEntity>

    @Query("SELECT trackId FROM fav_tracks_table")
    suspend fun getFavTracksId(): List<Int>
}