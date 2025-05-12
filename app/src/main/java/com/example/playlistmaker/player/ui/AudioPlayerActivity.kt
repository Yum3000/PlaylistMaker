package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel : PlayerViewModel

    private lateinit var binding: ActivityAudioplayerBinding

    private var playerState = PlayerState.DEFAULT

    private companion object {
        const val INTENT_TRACK_KEY = "track_to_player"
        const val ERROR_TRACK_ID = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        val trackId: Int = this.intent.getIntExtra(INTENT_TRACK_KEY, ERROR_TRACK_ID)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.getViewModelFactory(trackId)
        )[PlayerViewModel::class.java]

        viewModel.getPlayerStateLiveData().observe(this) { state ->
            playerState = state.playerState
            redrawPlayer(state.playerState, state.curPosition)
            redrawTrack(state.trackInfo)
        }

        viewModel.getPlayerErrorToast().observe(this) {
            showToast()
        }

        binding.playBtn.setOnClickListener {
            viewModel.handlePlayBtnClick()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    private fun redrawPlayer(state: PlayerState, curPos: String) {
        binding.currentTrackTimer.text = curPos
        when (state) {
            PlayerState.PLAYING -> {
                binding.playBtn.setImageResource(R.drawable.pause_btn)
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                binding.playBtn.setImageResource(R.drawable.play_btn)
            }
            else -> { }
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
            this,
            R.string.playing_error,
            Toast.LENGTH_LONG
        ).show()
    }
}