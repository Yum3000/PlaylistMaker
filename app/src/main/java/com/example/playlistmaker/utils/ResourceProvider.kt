package com.example.playlistmaker.utils

import android.content.Context

class ResourceProvider(private val context: Context) {
    fun getString(resId: Int, vararg args: Any?): String {
        return context.getString(resId, *args)
    }
}