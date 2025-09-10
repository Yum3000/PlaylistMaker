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

    @Query("SELECT * FROM added_playlist_tracks_table WHERE trackId IN (:tracksIds)")
    suspend fun getAllTracks(tracksIds: List<Int>): List<AddedTrackToPlaylistEntity>

    @Query("DELETE FROM added_playlist_tracks_table WHERE trackId NOT IN (:tracksIds)")
    suspend fun deleteUnusedTracks(tracksIds: List<Int>)
}