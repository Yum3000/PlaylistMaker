package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist)

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun getTrackCount(playlistId: Int): Int

    suspend fun getPlaylistById(playlistId: Int): Playlist?

    suspend fun getTracksByIds(tracksIds: List<Int>): Flow<List<Track>>

    suspend fun deleteTrack(trackId: Int, playlist: Playlist)

    suspend fun deletePlaylist(playlistId: Int)

    suspend fun updatePlaylist(playlist: Playlist)
}