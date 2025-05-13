package com.example.playlistmaker.sharing.domain.models

data class EmailData(
    val subject: String,
    val message: String,
    val sendTo: String,
)
