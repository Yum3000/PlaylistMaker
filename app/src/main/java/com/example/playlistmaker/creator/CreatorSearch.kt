package com.example.playlistmaker.creator

import com.example.playlistmaker.search.data.MusicRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.MusicRepository
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl

object CreatorSearch {
    private fun getMusicRepository(): MusicRepository {
        return MusicRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getMusicRepository())
    }
}