package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPref: SharedPreferences) {
    val KEY_FOR_SEARCH_HISTORY = "KEY"

    val history: MutableList<Track> = mutableListOf()

    init {
        val json = sharedPref.getString(KEY_FOR_SEARCH_HISTORY, null)
        if (json != null) {
            history.addAll(Gson().fromJson(json, Array<Track>::class.java))
        }
    }

    fun updateHistory(newTrack: Track) {
        history.removeIf { it.trackId == newTrack.trackId }
        history.add(0, newTrack)

        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveHistory()
    }

    private fun saveHistory() {
        val json = Gson().toJson(history)
        sharedPref.edit().putString(KEY_FOR_SEARCH_HISTORY, json).apply()
    }

    fun clearHistory() {
        history.clear()
        saveHistory()
    }
}