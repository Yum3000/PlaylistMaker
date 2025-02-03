package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackTitle: TextView = itemView.findViewById(R.id.track_title)
    private val trackArtist: TextView = itemView.findViewById(R.id.track_artist)
    private val trackDuration: TextView = itemView.findViewById(R.id.track_duration)
    private val trackCover: ImageView = this.itemView.findViewById(R.id.track_cover)

    fun bind(track: Track) {
        trackTitle.text = track.trackName
        trackArtist.text = track.artistName
        trackDuration.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(2))
            .centerCrop()
            .into(trackCover)
    }
}