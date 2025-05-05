package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.NetworkResponse
import com.example.playlistmaker.data.dto.TracksRequest
import com.example.playlistmaker.data.dto.TracksResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitNetworkClient: NetworkClient {

    private val url = "https://itunes.apple.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val musicAPIService: MusicAPIService = retrofit.create<MusicAPIService>()

    override fun doRequest(dto: Any): NetworkResponse {

        if (dto is TracksRequest) {
            var resp: retrofit2.Response<TracksResponse>? = null
            try {
                resp = musicAPIService.searchTracks(dto.expression).execute()
            } catch (e: Exception){
            }

            val body = resp?.body() ?: NetworkResponse()

            return body.apply { resultCode = resp?.code() ?: 500}
        } else {
            return NetworkResponse().apply { resultCode = 400 }
        }
    }
}