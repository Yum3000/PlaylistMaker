package com.example.playlistmaker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(
    var tracks: MutableList<Track>,
    private val onTrackClick: (Track) -> Unit = {_ ->}
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onTrackClick(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}