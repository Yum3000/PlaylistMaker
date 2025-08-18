package com.example.playlistmaker.media.data.db.converters

import com.example.playlistmaker.media.data.db.entity.FavouriteTrackEntity
import com.example.playlistmaker.search.domain.models.Track

class FavTrackDbConvertor {

    fun map(track: Track): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            track.trackId, track.trackName, track.collectionName, track.artistName,
            track.trackTimeMillis, track.releaseDate, track.primaryGenreName, track.country,
            track.artworkUrl100, track.previewUrl
        )
    }

    fun map(track: FavouriteTrackEntity): Track {
        return Track(
            track.trackId, track.trackName, track.collectionName, track.artistName,
            track.trackTime, track.releaseDate, track.primaryGenreName, track.country,
            track.artworkUrl100, track.previewUrl
        )
    }
}