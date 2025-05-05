package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.SettingsManagerImpl
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsManager
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl

object CreatorSettings {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getSettingsManager(): SettingsManager {
        return SettingsManagerImpl(application)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsManager())
    }
}