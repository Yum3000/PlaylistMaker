package com.example.playlistmaker.di

import com.example.playlistmaker.media.presentation.FavouritesViewModel
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel { (trackId: Int) ->
        PlayerViewModel(trackId, get(), get())
    }

    viewModel {
        FavouritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}