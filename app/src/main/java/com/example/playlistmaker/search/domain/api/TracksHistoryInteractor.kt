package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksHistoryInteractor {
    fun getHistory(): List<Track>
    fun updateHistory(newTrack: Track)
    fun clearHistory()
}