package com.example.playlistmaker.search.ui

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.SearchTrackInfo

class TrackViewHolder(private val binding: TrackViewBinding) : RecyclerView.ViewHolder(binding.root) {

    private val cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f,
        itemView.resources.displayMetrics).toInt()

    fun bind(track: SearchTrackInfo) {
        binding.trackTitle.text = track.trackName ?: ""
        binding.trackArtist.text = track.artistName ?: ""
        binding.trackArtist.requestLayout()
        binding.trackDuration.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl ?: "")
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(cornerRadius))
            .centerCrop()
            .into(binding.trackCover)
    }
}