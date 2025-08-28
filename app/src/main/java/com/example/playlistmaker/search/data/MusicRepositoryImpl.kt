package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.dto.TracksRequest
import com.example.playlistmaker.search.data.dto.TracksResponse
import com.example.playlistmaker.search.domain.api.MusicRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MusicRepositoryImpl (private val networkClient: NetworkClient,
                           private val sharedPref: SharedPreferences? = null,
                           private val gson: Gson? = null,
                           private val appDatabase: AppDatabase) : MusicRepository {

    private val history = mutableListOf<Track>()

    init {
        if (sharedPref != null && gson != null) {
            val json = sharedPref.getString(KEY_FOR_SEARCH_HISTORY, null)
            if (json != null) {
                history.addAll(gson.fromJson(json, Array<Track>::class.java))
            }
        }
    }

    override fun searchTracks(expression: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequest(TracksRequest(expression))
        when (response.resultCode) {
            200 -> {
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
                    )
                }

                val favTracksId = appDatabase.favTracksDao().getFavTracksId()
                tracks.forEach{ track ->
                    if (track.trackId in favTracksId) track.isFavourite = true
                }
                emit(tracks)
            }

            else -> {
                emit(null)
            }
        }
    }

    override suspend fun getHistory(): List<Track> {
        val favTracksId = withContext(Dispatchers.IO) {
            appDatabase.favTracksDao().getFavTracksId()
        }
        history.forEach{ track ->
            if (track.trackId in favTracksId) track.isFavourite = true
        }
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
        val json = gson?.toJson(history)
        sharedPref?.edit()?.putString(KEY_FOR_SEARCH_HISTORY, json)?.apply()
    }

    override fun clearHistory() {
        history.clear()
        saveHistory()
    }

    companion object {
        private const val KEY_FOR_SEARCH_HISTORY = "KEY_FOR_SEARCH_HISTORY"
    }
}