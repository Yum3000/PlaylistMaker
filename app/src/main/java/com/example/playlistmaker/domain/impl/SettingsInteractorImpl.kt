package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsManager

class SettingsInteractorImpl(private val settingsManager: SettingsManager) : SettingsInteractor {
    override fun getTheme(): Boolean {
        return settingsManager.getTheme()
    }

    override fun saveTheme(isDark: Boolean) {
        settingsManager.saveTheme(isDark)
    }
}