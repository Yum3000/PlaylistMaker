package com.example.playlistmaker.di

import android.content.res.Configuration
import com.example.playlistmaker.search.data.MusicRepositoryImpl
import com.example.playlistmaker.search.domain.api.MusicRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import com.example.playlistmaker.App.Companion.HISTORY_DI
import com.example.playlistmaker.App.Companion.SETTINGS_DI
import com.example.playlistmaker.media.data.db.FavTrackRepositoryImpl
import com.example.playlistmaker.media.data.db.converters.FavTrackDbConvertor
import com.example.playlistmaker.media.domain.db.FavTracksRepository
import com.example.playlistmaker.player.data.AudioPlayerManagerImpl
import com.example.playlistmaker.player.domain.api.AudioPlayerManager
import com.example.playlistmaker.settings.data.SettingsManagerImpl
import com.example.playlistmaker.settings.domain.api.SettingsManager
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidApplication

val repositoryModule = module {

    single<MusicRepository> {
        MusicRepositoryImpl(get(), get(named(HISTORY_DI)), get(), get())
    }

    single <AudioPlayerManager> {
        AudioPlayerManagerImpl(get())
    }

    single<SettingsManager> {
        val isDarkMode = androidApplication().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        SettingsManagerImpl(isDarkMode, get(named(SETTINGS_DI)))
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidApplication())
    }

    factory { FavTrackDbConvertor() }

    single<FavTracksRepository> { FavTrackRepositoryImpl(get(), get()) }
}