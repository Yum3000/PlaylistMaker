package com.example.playlistmaker.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.TracksRequest
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.domain.api.MusicRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class MusicRepositoryImpl (private val networkClient: NetworkClient,
                           application: Application? = null
) : MusicRepository {

    private val history = mutableListOf<Track>()
    private val gson = Gson()

    private val sharedPref: SharedPreferences? = application?.getSharedPreferences(
        HISTORY_SHARED_PREFERENCES, Context.MODE_PRIVATE)

    init {

        val json = sharedPref?.getString(KEY_FOR_SEARCH_HISTORY, null)
        if (json != null) {
            history.addAll(gson.fromJson(json, Array<Track>::class.java))
        }
    }

    override fun searchTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(TracksRequest(expression))
        if (response.resultCode == 200) {
            val tracks = (response as TracksResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.collectionName,
                    it.artistName,
                    it.getTrackTimeString(),
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.artworkUrl100,
                    it.previewUrl
                ) }
            tracks.forEach { updateHistory(it) }
            return tracks
        } else {
            return null
        }
    }

    override fun getHistory(): List<Track> {
        return history
    }

    override fun updateHistory(newTrack: Track) {
        history.removeIf { it.trackId == newTrack.trackId }
        history.add(0, newTrack)

        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveHistory()
    }

    private fun saveHistory() {
        val json = gson.toJson(history)
        sharedPref?.edit()?.putString(KEY_FOR_SEARCH_HISTORY, json)?.apply()
    }

    override fun clearHistory() {
        history.clear()
        saveHistory()
    }

    companion object {
        private const val KEY_FOR_SEARCH_HISTORY = "KEY"
        private const val HISTORY_SHARED_PREFERENCES = "sh_pr_history"
    }
}