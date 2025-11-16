package com.example.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.settings.ui.compose.SettingsScreen

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val initialModeDark = isNightMode()
        return ComposeView(requireContext()).apply {

            setContent {
                var themeDark by remember {mutableStateOf(initialModeDark)}
                AppTheme(themeDark) {
                    SettingsScreen(
                        {
                            themeDark = it
                        }
                    )
                }
            }
        }
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }
}