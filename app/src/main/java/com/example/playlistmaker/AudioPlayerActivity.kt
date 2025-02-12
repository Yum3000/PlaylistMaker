package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.App.Companion.INTENT_TRACK_KEY
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayerActivity : AppCompatActivity() {

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
        val playBtn: ImageButton = findViewById(R.id.playBtn)
        val addToFavBtn: ImageButton = findViewById(R.id.addFavBtn)
        val currentTrackTimer: TextView = findViewById(R.id.currentTrackTimer)

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
        durTrack.text = track?.getTimeInMinSec() ?: getString(R.string.no_data)
        albumTrack.text = track?.collectionName ?: getString(R.string.no_data)
        releaseDataTrack.text = track?.getTrackReleaseDate() ?: getString(R.string.no_data)
        genreTrack.text = track?.primaryGenreName ?: getString(R.string.no_data)
        countryTrack.text = track?.country ?: getString(R.string.no_data)

    }
}