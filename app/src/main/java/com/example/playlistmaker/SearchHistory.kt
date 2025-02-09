package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPref: SharedPreferences) {

    val history = mutableListOf<Track>()
    private val gson = Gson()

    init {
        val json = sharedPref.getString(KEY_FOR_SEARCH_HISTORY, null)
        if (json != null) {
            history.addAll(gson.fromJson(json, Array<Track>::class.java))
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
        val json = gson.toJson(history)
        sharedPref.edit().putString(KEY_FOR_SEARCH_HISTORY, json).apply()
    }

    fun clearHistory() {
        history.clear()
        saveHistory()
    }

    companion object{
        const val KEY_FOR_SEARCH_HISTORY = "KEY"
    }
}