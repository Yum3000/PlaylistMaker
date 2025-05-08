package com.example.playlistmaker

import com.example.playlistmaker.data.MusicRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.MusicRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object CreatorSearch {
    private fun getMusicRepository(): MusicRepository {
        return MusicRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getMusicRepository())
    }
}