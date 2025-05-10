package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.creator.CreatorSettings
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class App : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        CreatorSettings.initApplication(this)
        CreatorHistory.initApplication(this)
        settingsInteractor = CreatorSettings.provideSettingsInteractor()
        val isDarkTheme = settingsInteractor.getTheme()
        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        settingsInteractor.saveTheme(darkThemeEnabled)
    }
}