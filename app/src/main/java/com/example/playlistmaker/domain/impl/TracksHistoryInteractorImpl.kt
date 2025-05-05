package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MusicRepository
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.models.Track

class TracksHistoryInteractorImpl(private val repository: MusicRepository) : TracksHistoryInteractor {
    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun updateHistory(newTrack: Track) {
        repository.updateHistory(newTrack)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}

