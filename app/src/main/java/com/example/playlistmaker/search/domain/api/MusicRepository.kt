package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun searchTracks(expression: String): Flow<List<Track>?>
    suspend fun getHistory(): List<Track>
    fun updateHistory(newTrack: Track)
    fun clearHistory()
}