package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackViewHolder

class PlaylistDetailAdapter(
    private val onTrackClick: (ListTrackInfo) -> Unit,
    private val onTrackLongClick: (ListTrackInfo) -> Boolean,
): RecyclerView.Adapter<TrackViewHolder>() {

    private val tracks: MutableList<Track> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackViewBinding.inflate(
            layoutInspector, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        val listTrackInfo = ListTrackInfo.trackToListTrackInfo(tracks[position])
        holder.bind(listTrackInfo)

        holder.itemView.setOnClickListener {
            onTrackClick(listTrackInfo)
        }

        holder.itemView.setOnLongClickListener {
            onTrackLongClick(listTrackInfo)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        this.notifyDataSetChanged()
    }
}