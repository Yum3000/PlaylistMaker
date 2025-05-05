package com.example.playlistmaker.ui.audioPlayer

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.CreatorAudioPlayer
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private companion object {
        const val INTENT_TRACK_KEY = "track_to_player"
    }

    enum class PlayerState {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    private var playerState = PlayerState.DEFAULT
    private var audioPlayerInteractor = CreatorAudioPlayer.provideAudioPlayerInteractor()
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var currentTrackTimer: TextView
    private lateinit var playBtn: ImageButton

    private val updateTrackTimeRunnable = object : Runnable {
        override fun run() {
            if (playerState == PlayerState.PLAYING) {
                currentTrackTimer.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(audioPlayerInteractor.getCurrentPosition())
                handler.postDelayed(this, 500L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            this.intent.getSerializableExtra(INTENT_TRACK_KEY, Track::class.java)
        else
            this.intent.getSerializableExtra(INTENT_TRACK_KEY) as Track

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val coverTrack: ImageView = findViewById(R.id.coverTrack)
        val trackName: TextView = findViewById(R.id.trackName)
        val artistName: TextView = findViewById(R.id.artistName)

        val addToPlaylistBtn: ImageButton = findViewById(R.id.addBtn)
        playBtn = findViewById(R.id.playBtn)
        playBtn.isEnabled = false
        val addToFavBtn: ImageButton = findViewById(R.id.addFavBtn)
        currentTrackTimer = findViewById(R.id.currentTrackTimer)

        val durTrack: TextView = findViewById(R.id.durationTV)
        val albumTrack: TextView = findViewById(R.id.albumTV)
        val releaseDataTrack: TextView = findViewById(R.id.yearTV)
        val genreTrack: TextView = findViewById(R.id.genreTV)
        val countryTrack: TextView = findViewById(R.id.countryTV)

        Glide.with(this)
            .load(track?.getCoverArtwork() ?: "")
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.cover_placeholder)
            .into(coverTrack)

        trackName.text = track?.trackName ?: getString(R.string.no_data)
        artistName.text = track?.artistName ?: getString(R.string.no_data)
        currentTrackTimer.text = getString(R.string.track_timer_ph)
        durTrack.text = track?.trackTimeMillis ?: getString(R.string.no_data)
        albumTrack.text = track?.collectionName ?: getString(R.string.no_data)
        releaseDataTrack.text = track?.getTrackReleaseDate() ?: getString(R.string.no_data)
        genreTrack.text = track?.primaryGenreName ?: getString(R.string.no_data)
        countryTrack.text = track?.country ?: getString(R.string.no_data)

        track?.previewUrl?.let { url ->
            preparePlayer(url)
        }

        playBtn.setOnClickListener {
            if (playerState != PlayerState.DEFAULT) {
                playerControl()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.playing_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerInteractor.release()
        handler?.removeCallbacks(updateTrackTimeRunnable)
    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayback()
        playBtn.setImageResource(R.drawable.pause_btn)
        playerState = PlayerState.PLAYING
        handler?.post(updateTrackTimeRunnable)
    }

    private fun pausePlayer() {
        audioPlayerInteractor.pausePlayback()
        playBtn.setImageResource(R.drawable.play_btn)
        playerState = PlayerState.PAUSED
        handler?.removeCallbacks(updateTrackTimeRunnable)
    }

    private fun preparePlayer(trackUrl: String) {
        audioPlayerInteractor.prepare(trackUrl) {
            playBtn.isEnabled = true
            playerState = PlayerState.PREPARED
        }
        audioPlayerInteractor.setOnCompletionListener {
            playBtn.setImageResource(R.drawable.play_btn)
            playerState = PlayerState.PREPARED
            currentTrackTimer.text = getString(R.string.track_timer_ph)
            handler.removeCallbacks(updateTrackTimeRunnable)
        }
    }

    private fun playerControl() {
        when (playerState) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
            }
            else -> { }
        }
    }
}