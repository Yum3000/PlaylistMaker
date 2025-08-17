package com.example.playlistmaker.search.domain.models

import android.icu.text.SimpleDateFormat
import java.io.Serializable
import java.util.Locale

data class Track(
    val trackId: Int?,
    val trackName: String?,
    val collectionName: String?,
    val artistName: String?,
    val trackTimeMillis: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    var isFavourite: Boolean = false
) : Serializable {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")

    fun getTrackReleaseDate(): String? {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = releaseDate?.let { parser.parse(it) } ?: return null
        val formatter = SimpleDateFormat("yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}