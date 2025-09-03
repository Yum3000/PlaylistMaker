package com.example.playlistmaker.player.ui

sealed class AddTrackStatus {
    object Added: AddTrackStatus()
    object Exists: AddTrackStatus()
}