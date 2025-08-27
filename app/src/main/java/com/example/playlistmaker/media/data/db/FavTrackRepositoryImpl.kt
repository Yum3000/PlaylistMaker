package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.db.converters.FavTrackDbConvertor
import com.example.playlistmaker.media.data.db.dao.FavTracksDao
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTrackRepositoryImpl(
    private val favTracksDao: FavTracksDao,
    private val favTrackDbConvertor: FavTrackDbConvertor
): FavTracksRepository {
    override suspend fun addFavTrack(track: Track) {
        favTracksDao.addToFavourites(convertFromTrack(track))
    }

    override suspend fun removeFavTrack(track: Track) {
        favTracksDao.removeFromFavourites(convertFromTrack(track))
    }

    override suspend fun getFavTracks(): Flow<List<Track>> = flow {
        val tracks = favTracksDao.getFavTracks()
        emit(convertFromFavTracksEntity(tracks))
    }

    override suspend fun getFavTracksId(): Flow<List<Int>> = flow {
        val tracksId = favTracksDao.getFavTracksId()
        emit(tracksId)
    }

    override suspend fun getFavTrackById(id: Int): Track? {
        val track = favTracksDao.getFavTrackById(id) ?: return null
        return favTrackDbConvertor.map(track)
    }

    private fun convertFromFavTracksEntity(tracks: List<FavouriteTrackEntity>): List<Track> {
        return tracks.map { track -> favTrackDbConvertor.map(track) }
    }

    private fun convertFromTrack(track: Track): FavouriteTrackEntity {
        return favTrackDbConvertor.map(track)
    }
}