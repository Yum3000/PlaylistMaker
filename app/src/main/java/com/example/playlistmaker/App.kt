package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.creator.CreatorExternalNavigator
import com.example.playlistmaker.creator.CreatorHistory
import com.example.playlistmaker.creator.CreatorSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        CreatorSettings.initApplication(this)
        CreatorSettings.initTheme(resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        CreatorHistory.initApplication(this)
        CreatorExternalNavigator.initApplication(this)
        val settingsInteractor = CreatorSettings.provideSettingsInteractor()
        settingsInteractor.setSavedTheme()
    }
}