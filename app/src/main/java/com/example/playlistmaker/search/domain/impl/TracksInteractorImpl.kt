package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.MusicRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl (private val repository: MusicRepository) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>?> {
        return repository.searchTracks(expression)
    }
}