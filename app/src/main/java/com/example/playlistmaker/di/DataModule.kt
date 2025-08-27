package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.App.Companion.HISTORY_DI
import com.example.playlistmaker.App.Companion.SETTINGS_DI
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.dao.FavTracksDao
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.network.MusicAPIService
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val URL_ITUNES = "https://itunes.apple.com"
private const val SETTINGS_SHARED_PREFERENCES = "shared_preferences"
private const val HISTORY_SHARED_PREFERENCES = "sh_pr_history"

val dataModule = module {
    single<MusicAPIService> {
        Retrofit.Builder()
            .baseUrl(URL_ITUNES)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicAPIService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single(named(SETTINGS_DI)) {
        androidContext().getSharedPreferences(SETTINGS_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    single(named(HISTORY_DI)) {
        androidContext().getSharedPreferences(HISTORY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    factory { MediaPlayer() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "playlistMakerDB.db")
            .build()
    }

    single { get<AppDatabase>().favTracksDao() as FavTracksDao }

}