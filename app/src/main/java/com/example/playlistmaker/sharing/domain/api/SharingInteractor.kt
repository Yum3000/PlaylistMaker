package com.example.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun shareApp(link: String)
    fun openTerms(link: String)
    fun writeToSupport(subject: String, message: String, sendTo: String)
}