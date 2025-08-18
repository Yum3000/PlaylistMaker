package com.example.playlistmaker.media.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.media.presentation.FavouritesViewModel
import com.example.playlistmaker.media.presentation.MediaScreenFavouritesState
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavourites: Fragment() {

    private val favouritesViewModel: FavouritesViewModel by viewModel()

    private val favTracksAdapter = TrackAdapter(mutableListOf()) {track, _ ->
        favouritesViewModel.handleTrackClick(track.trackId)
    }

    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favouritesViewModel.updateFavouriteList()
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewFavTracks.adapter = favTracksAdapter
        favouritesViewModel.observeStateFavourites().observe(viewLifecycleOwner){
            when(it){
                is MediaScreenFavouritesState.Content -> showContent(it.tracks)
                is MediaScreenFavouritesState.Empty -> showEmpty()
                is MediaScreenFavouritesState.Loading -> showLoading()
            }
        }

        favouritesViewModel.getTrackIdToOpenPlayer().observe(viewLifecycleOwner) {
                trackId -> openPlayerActivity(trackId)
        }
    }

    private fun showContent(tracks: List<Track>) {
        binding.progressBar.isVisible = false
        binding.messageView.root.isVisible = false

        favTracksAdapter.tracks.clear()
        val listTrackInfo = tracks.map { ListTrackInfo.trackToListTrackInfo(it) }
        favTracksAdapter.tracks.addAll(listTrackInfo)

        binding.recyclerViewFavTracks.isVisible = true

        favTracksAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        binding.messageView.root.isVisible = true
        binding.recyclerViewFavTracks.isVisible = false
        binding.messageView.placeholderMessage.text = requireActivity().getString(R.string.no_favourites)

        val imageResource = getPlaceholderImageResource()
        binding.messageView.placeholderImage.setImageResource(imageResource)
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.messageView.root.isVisible = false
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

    private fun openPlayerActivity(trackId: Int) {
        val bundle = AudioPlayerFragment.createArgs(trackId)
        findNavController().navigate(R.id.action_mediaFragment_to_audioPlayerFragment, bundle)
    }

    companion object {
        fun newInstance() : FragmentFavourites = FragmentFavourites()
    }
}