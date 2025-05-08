package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MusicRepository
import com.example.playlistmaker.domain.api.TracksInteractor

class TracksInteractorImpl (private val repository: MusicRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        val t = Thread {
            consumer.consume(repository.searchTracks(expression))
        }
        t.start()
    }
}