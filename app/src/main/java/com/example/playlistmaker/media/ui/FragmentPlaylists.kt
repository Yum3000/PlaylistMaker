package com.example.playlistmaker.media.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.presentation.MediaScreenPlaylistsState
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists: Fragment() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private lateinit var binding: FragmentPlaylistsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.observeStatePlaylists().observe(viewLifecycleOwner){
            when(it){
                MediaScreenPlaylistsState.Content -> showContent()
                MediaScreenPlaylistsState.Empty -> showEmpty()
            }
        }
    }

    private fun showContent(){
        binding.messageView.root.isVisible = false
    }

    private fun showEmpty(){
        binding.messageView.root.isVisible = true
        binding.messageView.placeholderMessage.text = requireActivity().getString(R.string.no_playlists)

        val imageResource = getPlaceholderImageResource()
        binding.messageView.placeholderImage.setImageResource(imageResource)

    }

    private fun getPlaceholderImageResource(): Int {
        return if (isNightMode()) {
            R.drawable.no_results_icon_dark
        } else {
            R.drawable.no_results_icon_light
        }
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        fun newInstance() : FragmentPlaylists = FragmentPlaylists()
    }
}