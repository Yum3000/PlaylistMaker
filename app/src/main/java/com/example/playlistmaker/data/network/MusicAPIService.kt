package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TracksResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicAPIService {

    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TracksResponse>
}