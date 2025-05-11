package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.SettingsManagerImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsManager
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import kotlin.properties.Delegates

object CreatorSettings {
    private lateinit var application: Application

    private var isDarkTheme by Delegates.notNull<Boolean>()

    fun initApplication(application: Application) {
        CreatorSettings.application = application
    }

    fun initTheme(isDarkMode: Boolean) {
        this.isDarkTheme = isDarkMode
    }

    private fun getSharedPreference() : SharedPreferences {
        return application.getSharedPreferences(
            SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getSettingsManager(): SettingsManager {
        return SettingsManagerImpl(isDarkTheme, getSharedPreference())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsManager())
    }

    private const val SETTINGS_SHARED_PREFERENCES = "shared_preferences"
}