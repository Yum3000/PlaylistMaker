package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsManager

class SettingsManagerImpl (private val isDarkMode: Boolean,
                           private val sharedPref: SharedPreferences) : SettingsManager {

    override fun getTheme(): Boolean {
        return sharedPref.getBoolean(DARK_THEME_KEY, isDarkMode)
    }

    override fun saveTheme(isDark: Boolean) {
        sharedPref.edit()
            .putBoolean(DARK_THEME_KEY, isDark)
            .apply()
    }

    override fun setSavedTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (getTheme()) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val DARK_THEME_KEY = "is_dark_theme"
    }
}

