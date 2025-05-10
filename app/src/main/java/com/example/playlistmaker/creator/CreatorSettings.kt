package com.example.playlistmaker.creator

import android.app.Application
import com.example.playlistmaker.settings.domain.impl.SettingsManagerImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsManager
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl

object CreatorSettings {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        CreatorSettings.application = application
    }

    private fun getSettingsManager(): SettingsManager {
        return SettingsManagerImpl(application)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsManager())
    }
}