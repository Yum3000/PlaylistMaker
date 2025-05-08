package com.example.playlistmaker.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import com.example.playlistmaker.domain.api.SettingsManager

class SettingsManagerImpl (private val application: Application) : SettingsManager {
    private val sharedPref: SharedPreferences = application.getSharedPreferences(
        SH_PREF_FILE, Context.MODE_PRIVATE
    )

    override fun getTheme(): Boolean {
        val isDarkMode = application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
        return sharedPref.getBoolean(DARK_THEME_KEY, isDarkMode)
    }

    override fun saveTheme(isDark: Boolean) {
        sharedPref.edit()
            .putBoolean(DARK_THEME_KEY, isDark)
            .apply()
    }

    companion object {
        const val SH_PREF_FILE = "shared_preferences"
        const val DARK_THEME_KEY = "is_dark_theme"
        const val INTENT_TRACK_KEY = "track_to_player"
    }
}

