package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.ui.compose.SearchScreen

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    SearchScreen({ openPlayerFragment(it) })
                }
            }
        }
    }

    private fun openPlayerFragment(trackId: Int) {
        val bundle = AudioPlayerFragment.createArgs(trackId)
        findNavController().navigate(R.id.action_searchFragment_to_audioPlayerFragment, bundle)
    }
}