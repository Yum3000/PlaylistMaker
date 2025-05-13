package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.SingleLiveEvent
import com.example.playlistmaker.creator.CreatorExternalNavigator
import com.example.playlistmaker.creator.CreatorSettings
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val settingsThemeDarkLiveData = SingleLiveEvent<Boolean>()

    init {
        settingsThemeDarkLiveData.value = settingsInteractor.getTheme()
    }

    fun getSettingsThemeDarkLiveData(): LiveData<Boolean> = settingsThemeDarkLiveData

    fun switchTheme(isDark: Boolean) {
        settingsInteractor.saveTheme(isDark)
        settingsInteractor.setSavedTheme()
    }

    fun shareApp(link: String) {
        sharingInteractor.shareApp(link)
    }

    fun writeToSupport(subject: String, message: String, sendTo: String) {
        sharingInteractor.writeToSupport(subject, message, sendTo)
    }

    fun openUserAgreement(link: String) {
        sharingInteractor.openTerms(link)
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    CreatorExternalNavigator.provideSharingInteractor(),
                    CreatorSettings.provideSettingsInteractor()
                )
            }
        }
    }
}