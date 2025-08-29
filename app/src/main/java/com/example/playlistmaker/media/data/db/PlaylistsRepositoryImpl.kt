package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.dao.PlaylistsDao
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val playlistDbConvertor: PlaylistDbConvertor): PlaylistsRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsDao.createPlaylist(convertPlaylistToEntity(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistsDao.getPlaylists()
        emit(convertFromPlaylistsEntity(playlists))
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }
}