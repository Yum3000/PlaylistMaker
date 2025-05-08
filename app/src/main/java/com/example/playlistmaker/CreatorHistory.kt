package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.MusicRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.MusicRepository
import com.example.playlistmaker.domain.impl.TracksHistoryInteractorImpl

object CreatorHistory {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getMusicRepositoryHistory(): MusicRepository {
        return MusicRepositoryImpl(RetrofitNetworkClient(), application = application)
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getMusicRepositoryHistory())
    }
}