package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.compose.MediaScreen
import com.example.playlistmaker.player.ui.AudioPlayerFragment

class MediaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {

                AppTheme {
                    MediaScreen(
                        openAudioPlayerScreen = { openPlayerFragment(it) },
                        openPlaylistScreen = { openPlaylistScreen(it) },
                        openToCreateNewPlaylistScreen = {
                            createNewPlaylistScreen()
                        },
                    )
                }
            }
        }
    }

    private fun openPlayerFragment(trackId: Int) {
        val bundle = AudioPlayerFragment.createArgs(trackId)
        findNavController().navigate(R.id.action_mediaFragment_to_audioPlayerFragment, bundle)
    }

    private fun openPlaylistScreen(playlistId: Int) {
        val bundle = PlaylistFragment.createArgs(playlistId)
        findNavController().navigate(R.id.action_mediaFragment_to_playlistFragment, bundle)
    }

    private fun createNewPlaylistScreen() {
        findNavController().navigate(R.id.action_mediaFragment_to_fragmentCreatePlaylist)
    }
}

