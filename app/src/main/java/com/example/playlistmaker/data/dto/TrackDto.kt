package com.example.playlistmaker.data.dto

import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto (
    val trackId: Int?,
    val trackName: String?,
    val collectionName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String,
    val artworkUrl100: String?,
    val previewUrl: String?
) {
    fun getTrackTimeString(): String? {
        return if (trackTimeMillis != null) {
            SimpleDateFormat(TRACK_TIME_FORMAT, Locale.getDefault()).format(trackTimeMillis)
        } else null
    }

    private companion object {
        const val TRACK_TIME_FORMAT = "mm:ss"
    }
}