package com.example.playlistmaker.domain.api

interface SettingsManager {
    fun getTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}