package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicAPIService {

    @GET("/search?entity=song")
    suspend fun searchTracks(@Query("term") text: String): TracksResponse
}