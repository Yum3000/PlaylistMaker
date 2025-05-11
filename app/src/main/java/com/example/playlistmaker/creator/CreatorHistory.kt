package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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

    private fun getSharedPreference() : SharedPreferences {
        return application.getSharedPreferences(
            HISTORY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getMusicRepositoryHistory(): MusicRepository {
        return MusicRepositoryImpl(RetrofitNetworkClient(), getSharedPreference())
    }

    fun provideTracksHistoryInteractor(): TracksHistoryInteractor {
        return TracksHistoryInteractorImpl(getMusicRepositoryHistory())
    }

    private const val HISTORY_SHARED_PREFERENCES = "sh_pr_history"
}