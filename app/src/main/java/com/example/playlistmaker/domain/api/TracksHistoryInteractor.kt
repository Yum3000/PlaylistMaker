package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksHistoryInteractor {
    fun getHistory(): List<Track>
    fun updateHistory(newTrack: Track)
    fun clearHistory()
}