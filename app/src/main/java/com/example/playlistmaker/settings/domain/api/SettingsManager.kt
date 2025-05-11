package com.example.playlistmaker.settings.domain.api

interface SettingsManager {
    fun getTheme(): Boolean
    fun saveTheme(isDark: Boolean)
    fun setSavedTheme()
}