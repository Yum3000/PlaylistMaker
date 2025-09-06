package com.example.playlistmaker.media.ui

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistViewHolder(private val binding: PlaylistViewBinding):
    RecyclerView.ViewHolder(binding.root) {

    private val cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f,
        itemView.resources.displayMetrics).toInt()

    fun bind(playlist: Playlist) {
        binding.playlistTitle.text = playlist.title

        val context = itemView.context
        val counter = getTrackCountString(playlist.tracksCount, context)
        binding.tracksCount.text = counter

        val options = RequestOptions()
            .placeholder(R.drawable.cover_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))

        Glide.with(itemView)
            .load(playlist.coverPath)
            .apply(options)
            .into(binding.playlistCover)
    }

    private fun getTrackCountString(count: Int?, context: Context): String {
        val trackCount = count ?: 0
        return when {
            (trackCount % 10 == 1 && trackCount % 100 != 11) -> context.getString(R.string.tracks_count_single, trackCount)
            (trackCount % 10 in 2..4 && trackCount % 100 !in 12..14) -> context.getString(R.string.tracks_count_few, trackCount)
            else -> context.getString(R.string.tracks_count_many, trackCount)
        }
    }
}