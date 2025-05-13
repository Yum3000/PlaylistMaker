package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface MusicRepository {
    fun searchTracks(expression: String): List<Track>?
    fun getHistory(): List<Track>
    fun updateHistory(newTrack: Track)
    fun clearHistory()
}