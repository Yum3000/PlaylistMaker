package com.example.playlistmaker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class ConnectionBroadcastReceiver(
    private val onNetworkChange: (Boolean) -> Unit,
    private val isConnectionAvailable: (Context?) -> Boolean
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION) {
            val isConnected = isConnectionAvailable(context)
            onNetworkChange(isConnected)
        }
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        val connectivityManager = context?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    companion object {
        const val ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }
}