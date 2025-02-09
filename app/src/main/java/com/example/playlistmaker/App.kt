package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPref = getSharedPreferences(SH_PREF_FILE, MODE_PRIVATE)
        val isDarkMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
                Configuration.UI_MODE_NIGHT_YES
        darkTheme = sharedPref.getBoolean(DARK_THEME_KEY, isDarkMode)
        switchTheme(isDarkMode)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPref.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }

    companion object {
        const val SH_PREF_FILE = "shared_preferences"
        const val DARK_THEME_KEY = "is_dark_theme"
    }

}