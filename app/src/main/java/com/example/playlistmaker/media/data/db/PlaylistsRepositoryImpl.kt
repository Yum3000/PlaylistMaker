package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.dao.AddToPlaylistDao
import com.example.playlistmaker.media.data.db.dao.PlaylistsDao
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val addToPlaylistDao: AddToPlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor): PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsDao.createPlaylist(convertPlaylistToEntity(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistsDao.getPlaylists()
        emit(convertFromPlaylistsEntity(playlists))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {

        val currentTracksIdsList = playlist.tracksIdsList ?: emptyList()

        if (!currentTracksIdsList.contains(track.trackId)) {
            addToPlaylistDao.addToPlaylist(playlistDbConvertor.trackToAddedTrack(track))

            val updatedTracksIdsList = getUpdatedTracksIdsList(track, playlist)
            val newTracksCount = currentTracksIdsList.size + 1
            updatePlaylistTracks(playlist.id, updatedTracksIdsList, newTracksCount)
        }
    }

    override suspend fun getUpdatedTracksIdsList(track: Track, playlist: Playlist): String {
        val existingPlaylist = playlistsDao.getPlaylistById(playlist.id)
        val currentTracksIdsList = existingPlaylist?.tracksIdsList.let {
            playlistDbConvertor.deserializeTracksIdsList(it)
        } ?: emptyList()

        val updatedIds = (currentTracksIdsList + track.trackId).requireNoNulls().distinct()

        return playlistDbConvertor.serializeTracksIdsList(updatedIds)
    }

    override suspend fun updatePlaylistTracks(
        playlistId: Int, newTracksIdsList: String, tracksCount: Int) {
        addToPlaylistDao.updatePlaylistTracks(playlistId, newTracksIdsList, tracksCount)
    }

    override suspend fun getTrackCount(playlistId: Int): Int {
        val playlist = playlistsDao.getPlaylistById(playlistId)
        return playlist?.tracksCount ?: 0
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }
}