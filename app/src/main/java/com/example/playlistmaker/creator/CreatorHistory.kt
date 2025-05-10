package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.search.data.MusicRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.MusicRepository
import com.example.playlistmaker.search.domain.impl.TracksHistoryInteractorImpl

object CreatorHistory {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        CreatorHistory.application = application
    }

    private fun getMusicRepositoryHistory(): MusicRepository {
        return MusicRepositoryImpl(RetrofitNetworkClient(), application = application)
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getMusicRepositoryHistory())
    }
}