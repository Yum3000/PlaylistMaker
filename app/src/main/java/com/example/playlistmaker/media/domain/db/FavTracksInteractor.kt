package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavTracksInteractor {
    suspend fun addFavTrack(track: Track)

    suspend fun removeFavTrack(track: Track)

    suspend fun getFavTracks(): Flow<List<Track>>

    suspend fun getFavTracksId(): Flow<List<Int>>

    suspend fun getFavTrackById(id: Int): Track?

}