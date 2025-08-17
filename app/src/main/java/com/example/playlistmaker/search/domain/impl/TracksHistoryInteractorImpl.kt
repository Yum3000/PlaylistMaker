package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.MusicRepository
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: MusicRepository) :
    TracksHistoryInteractor {
    override suspend fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun updateHistory(newTrack: Track) {
        repository.updateHistory(newTrack)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}

