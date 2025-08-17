package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment: Fragment() {
    private var trackId: Int = ERROR_TRACK_ID

    private val viewModel: PlayerViewModel by lazy {
        getViewModel { parametersOf(trackId) }
    }

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private var playerState = PlayerState.DEFAULT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackId = requireArguments().getInt(INTENT_TRACK_KEY, ERROR_TRACK_ID)

        viewModel.getPlayerStateLiveData().observe(viewLifecycleOwner) { state ->
            playerState = state.playerState
            redrawPlayer(state.playerState, state.curPosition)
            redrawTrack(state.trackInfo)

            updateFavBtn(state.trackInfo.isFavourite)
        }

        viewModel.getPlayerErrorToast().observe(viewLifecycleOwner) {
            showToast()
        }

        binding.playBtn.setOnClickListener {
            viewModel.handlePlayBtnClick()
        }

        binding.addFavBtn.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun redrawPlayer(state: PlayerState, curPos: String?) {
        if (curPos == null) {
            binding.currentTrackTimer.text = getString(R.string.track_timer_ph)
        } else {
            binding.currentTrackTimer.text = curPos
        }

        when (state) {
            PlayerState.PLAYING -> {
                binding.playBtn.setImageResource(R.drawable.pause_btn)
            }

            PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.DEFAULT -> {
                binding.playBtn.setImageResource(R.drawable.play_btn)
            }
        }
    }

    private fun redrawTrack(track: PlayerTrackInfo) {
        Glide.with(this)
            .load(track.artworkUrl100 ?: "")
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.cover_placeholder)
            .into(binding.coverTrack)

        binding.trackName.text = track.trackName ?: getString(R.string.no_data)
        binding.artistName.text = track.artistName ?: getString(R.string.no_data)
        binding.durationTV.text = track.trackTime ?: getString(R.string.no_data)
        binding.albumTV.text = track.collectionName ?: getString(R.string.no_data)
        binding.yearTV.text = track.releaseDate ?: getString(R.string.no_data)
        binding.genreTV.text = track.primaryGenreName ?: getString(R.string.no_data)
        binding.countryTV.text = track.country ?: getString(R.string.no_data)
    }

    private fun showToast() {
        Toast.makeText(
            requireContext(),
            R.string.playing_error,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateFavBtn(isFavourite: Boolean) {
        val btnIcon = if (isFavourite) {
            R.drawable.add_to_fav_btn_active
        } else {
            R.drawable.add_to_fav_btn
        }
        binding.addFavBtn.setImageResource(btnIcon)
    }

    companion object {
        const val INTENT_TRACK_KEY = "track_to_player"
        const val ERROR_TRACK_ID = -1

        fun createArgs(trackId: Int): Bundle =
            bundleOf(INTENT_TRACK_KEY to trackId)
    }
}