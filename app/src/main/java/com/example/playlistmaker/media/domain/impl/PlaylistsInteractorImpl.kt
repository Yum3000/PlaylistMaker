package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.PlaylistsInteractor
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
): PlaylistsInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        playlistsRepository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun getTrackCount(playlistId: Int): Int {
        return playlistsRepository.getTrackCount(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        return playlistsRepository.getPlaylistById(playlistId)
    }

    override suspend fun getTracksByIds(tracksIds: List<Int>): Flow<List<Track>> {
        return playlistsRepository.getTracksByIds(tracksIds)
    }

    override suspend fun deleteTrack(trackId: Int, playlist: Playlist) {
        playlistsRepository.deleteTrack(trackId, playlist)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsRepository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlaylist(playlist)
    }
}