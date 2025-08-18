package com.example.playlistmaker.search.domain.models

data class ListTrackInfo(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl: String,
) {
    companion object {
        fun trackToListTrackInfo(track: Track?): ListTrackInfo {
            return ListTrackInfo(
                track?.trackId ?: -1,
                track?.trackName ?: "",
                track?.artistName ?: "",
                track?.trackTimeMillis ?: "",
                track?.artworkUrl100 ?: ""
            )
        }
    }
}