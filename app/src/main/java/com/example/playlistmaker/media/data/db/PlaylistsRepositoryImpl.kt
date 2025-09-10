package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.db.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.db.dao.AddToPlaylistDao
import com.example.playlistmaker.media.data.db.dao.PlaylistsDao
import com.example.playlistmaker.media.data.db.entity.AddedTrackToPlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.db.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val addToPlaylistDao: AddToPlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor
) : PlaylistsRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsDao.createOrUpdatePlaylist(convertPlaylistToEntity(playlist))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = playlistsDao.getPlaylists()
        emit(convertFromPlaylistsEntity(playlists))
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        val trackId = track.trackId ?: return

        val currentTracksIdsList = playlist.tracksIdsList ?: emptyList()

        if (!currentTracksIdsList.contains(trackId)) {
            addToPlaylistDao.addToPlaylist(playlistDbConvertor.trackToAddedTrack(track))

            val updatedTracksIdsList = getUpdatedTracksIdsList(trackId, playlist.id)
            updatePlaylistTracks(playlist.id, updatedTracksIdsList)
        }
    }

    override suspend fun getUpdatedTracksIdsList(trackId: Int, playlistId: Int): List<Int>? {
        val existingPlaylist = playlistsDao.getPlaylistById(playlistId)

        val currentTracksIdsList: MutableList<Int>? = existingPlaylist?.tracksIdsList.let {
            playlistDbConvertor.deserializeTracksIdsList(it)?.toMutableList()
        }

        if (currentTracksIdsList?.contains(trackId) == false) {
            currentTracksIdsList.add(trackId)
        } else {
            currentTracksIdsList?.remove(trackId)
        }

        return currentTracksIdsList?.distinct()
    }

    private suspend fun updatePlaylistTracks(
        playlistId: Int, newTracksIdsList: List<Int>?
    ) {
        val tracksCount = newTracksIdsList?.size ?: 0
        val newListStr = playlistDbConvertor.serializeTracksIdsList(newTracksIdsList)
        addToPlaylistDao.updatePlaylistTracks(playlistId, newListStr, tracksCount)
    }

    override suspend fun getTrackCount(playlistId: Int): Int {
        val playlist = playlistsDao.getPlaylistById(playlistId)
        return playlist?.tracksCount ?: 0
    }

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        val existingPlaylist = playlistsDao.getPlaylistById(playlistId)
        return existingPlaylist?.let {
            playlistDbConvertor.map(it)
        }
    }

    override suspend fun getTracksByIds(tracksIds: List<Int>): Flow<List<Track>> = flow {
        val listTracksEntities: List<AddedTrackToPlaylistEntity> =
            addToPlaylistDao.getAllTracks(tracksIds)
        val listTracks: List<Track> = listTracksEntities.map {
            playlistDbConvertor.addedTrackEntityToTrack(it)
        }

        val sortedTracks = tracksIds.mapNotNull { id ->
            listTracks.find { it.trackId == id }
        }.reversed()

        emit(sortedTracks)
    }

    override suspend fun deleteTrack(trackId: Int, playlist: Playlist) {
        val plTracks = getPlaylistById(playlist.id)?.tracksIdsList ?: emptyList()

        if (plTracks.contains(trackId)) {

            val updatedTracksIdsList = getUpdatedTracksIdsList(trackId, playlist.id)
            updatePlaylistTracks(playlist.id, updatedTracksIdsList)
            deleteUnusedTracks()
        }
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        playlistsDao.deletePlaylist(playlistId)
        deleteUnusedTracks()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val plEntity = convertPlaylistToEntity(playlist)

        playlistsDao.createOrUpdatePlaylist(plEntity)
    }

    private suspend fun deleteUnusedTracks() {
        val playlists = mutableListOf<Playlist>()
        getPlaylists().collect { pl -> playlists.addAll(pl) }

        val tracksIdsInUse = playlists.flatMap { it.tracksIdsList ?: emptyList() }.distinct()

        addToPlaylistDao.deleteUnusedTracks(tracksIdsInUse)
    }

    private fun convertFromPlaylistsEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConvertor.map(playlist) }
    }

    private fun convertPlaylistToEntity(playlist: Playlist): PlaylistEntity {
        return playlistDbConvertor.map(playlist)
    }
}