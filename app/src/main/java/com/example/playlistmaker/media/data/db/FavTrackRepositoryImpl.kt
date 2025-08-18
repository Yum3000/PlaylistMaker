package com.example.playlistmaker.media.data.db

import com.example.playlistmaker.media.data.db.converters.FavTrackDbConvertor
import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavTrackRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val favTrackDbConvertor: FavTrackDbConvertor
): FavTracksRepository {
    override suspend fun addFavTrack(track: Track) {
        appDatabase.favTracksDao().addToFavourites(
            convertFromTrack(track)
        )
    }

    override suspend fun removeFavTrack(track: Track) {
        appDatabase.favTracksDao().removeFromFavourites(
            convertFromTrack(track)
        )
    }

    override suspend fun getFavTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.favTracksDao().getFavTracks()
        emit(convertFromFavTracksEntity(tracks))
    }

    override suspend fun getFavTracksId(): Flow<List<Int>> = flow {
        val tracksId = appDatabase.favTracksDao().getFavTracksId()
        emit(tracksId)
    }

    private fun convertFromFavTracksEntity(tracks: List<FavouriteTrackEntity>): List<Track> {
        return tracks.map { track -> favTrackDbConvertor.map(track) }
    }

    private fun convertFromTrack(track: Track): FavouriteTrackEntity {
        return favTrackDbConvertor.map(track)
    }
}