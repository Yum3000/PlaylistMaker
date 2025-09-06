package com.example.playlistmaker.player.ui

sealed class AddTrackStatus {
    object Default: AddTrackStatus()
    data class Added(val playlistName: String?): AddTrackStatus()
    data class Exists(val playlistName: String?): AddTrackStatus()
}