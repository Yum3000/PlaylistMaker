package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp(link: String) {
        externalNavigator.shareLink(link)
    }

    override fun openTerms(link: String) {
        externalNavigator.openLink(link)
    }

    override fun writeToSupport(subject: String, message: String, sendTo: String) {
        externalNavigator.sendMail(EmailData(subject, message, sendTo))
    }

}
