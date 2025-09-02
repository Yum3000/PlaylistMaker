package com.example.playlistmaker.media.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistViewHolder(private val binding: PlaylistViewBinding):
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistTitle.text = playlist.title

        val context = itemView.context
        val message = context.getString(R.string.tracks_count, playlist.tracksCount)
        binding.tracksCount.text = message

        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(R.drawable.cover_placeholder)
            .centerCrop()
            .into(binding.playlistCover)
    }
}