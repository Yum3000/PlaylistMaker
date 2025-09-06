package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun getUpdatedTracksIdsList(track: Track, playlist: Playlist): String

    suspend fun updatePlaylistTracks(playlistId: Int, newTracksIdsList: String, tracksCount: Int)

    suspend fun getTrackCount(playlistId: Int): Int

}