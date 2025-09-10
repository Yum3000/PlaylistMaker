package com.example.playlistmaker.sharing.domain.api

interface SharingInteractor {
    fun shareLink(link: String)
    fun openTerms(link: String)
    fun writeToSupport(subject: String, message: String, sendTo: String)
}