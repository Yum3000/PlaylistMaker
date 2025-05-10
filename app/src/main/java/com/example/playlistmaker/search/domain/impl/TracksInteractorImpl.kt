package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.MusicRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor

class TracksInteractorImpl (private val repository: MusicRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            consumer.consume(repository.searchTracks(expression))
        }
        t.start()
    }
}