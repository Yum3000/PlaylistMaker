package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.NetworkResponse
import com.example.playlistmaker.search.data.dto.TracksRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val musicAPIService: MusicAPIService
    ): NetworkClient {

    override suspend fun doRequest(dto: Any): NetworkResponse {

        return if (dto is TracksRequest) {
            withContext(Dispatchers.IO) {
                val response = musicAPIService.searchTracks(dto.expression)
                try {
                    response.apply { resultCode = 200 }
                } catch (e: Throwable) {
                    response.apply { resultCode = 500 }
                }
            }
        } else {
            NetworkResponse().apply { resultCode = 400 }
        }
    }
}