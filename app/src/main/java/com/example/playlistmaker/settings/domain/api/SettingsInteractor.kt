package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun getTheme(): Boolean
    fun saveTheme(isDark: Boolean)
    fun setSavedTheme()
}