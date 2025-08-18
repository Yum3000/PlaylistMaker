package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.FavTracksInteractor
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavTracksInteractorImpl(
    private val favTrackRepository: FavTracksRepository
): FavTracksInteractor {
    override suspend fun addFavTrack(track: Track) {
        favTrackRepository.addFavTrack(track)
    }

    override suspend fun removeFavTrack(track: Track) {
        favTrackRepository.removeFavTrack(track)
    }

    override suspend fun getFavTracks(): Flow<List<Track>> {
        return favTrackRepository.getFavTracks()
    }

    override suspend fun getFavTracksId(): Flow<List<Int>> {
        return favTrackRepository.getFavTracksId()
    }
}