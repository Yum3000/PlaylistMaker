package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.AddedTrackToPlaylistEntity

@Dao
interface AddToPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToPlaylist(track: AddedTrackToPlaylistEntity)

    @Query("UPDATE playlists_table SET tracksIdsList = :newTracksIdsList, tracksCount = :tracksCount WHERE id = :playlistId")
    suspend fun updatePlaylistTracks(playlistId: Int, newTracksIdsList: String, tracksCount: Int)
}