package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.NetworkResponse
import com.example.playlistmaker.search.data.dto.TracksRequest
import com.example.playlistmaker.search.data.dto.TracksResponse

class RetrofitNetworkClient(
    private val musicAPIService: MusicAPIService
    ): NetworkClient {

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