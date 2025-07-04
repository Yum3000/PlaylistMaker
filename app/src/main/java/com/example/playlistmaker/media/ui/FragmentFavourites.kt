package com.example.playlistmaker.media.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.media.presentation.FavouritesViewModel
import com.example.playlistmaker.media.presentation.MediaScreenFavouritesState
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFavourites: Fragment() {

    companion object {
        fun newInstance() : FragmentFavourites = FragmentFavourites()
    }

    private val favouritesViewModel: FavouritesViewModel by viewModel()

    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritesViewModel.observeStateFavourites().observe(viewLifecycleOwner){
            when(it){
                MediaScreenFavouritesState.Content -> showContent()
                MediaScreenFavouritesState.Empty -> showEmpty()
            }
        }
    }

    private fun showContent(){
        binding.messageView.root.isVisible = false
    }

    private fun showEmpty(){
        binding.messageView.root.isVisible = true
        binding.messageView.placeholderMessage.text = requireActivity().getString(R.string.no_favourites)

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
}