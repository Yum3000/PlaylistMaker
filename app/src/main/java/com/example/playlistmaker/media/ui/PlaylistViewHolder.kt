package com.example.playlistmaker.media.ui

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding,
):
    RecyclerView.ViewHolder(binding.root) {

    private val cornerRadius = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,8f,
        itemView.resources.displayMetrics
    ).toInt()

    fun bind(playlist: Playlist) {
        binding.playlistTitle.text = playlist.title

        val trackCount = playlist.tracksCount ?: 0
        val count = getTrackCountString(trackCount, itemView.context)
        binding.tracksCount.text = count

        Glide.with(itemView)
            .load(playlist.coverPath)
            .placeholder(R.drawable.cover_placeholder)
            .transform( CenterCrop(), RoundedCorners(cornerRadius))
            .into(binding.playlistCover)
    }

    private fun getTrackCountString(count: Int?, context: Context): String {
        val trackCount = count ?: 0
        return when {
            (trackCount % 10 == 1 && trackCount % 100 != 11) -> context.getString(
                R.string.tracks_count_single,
                trackCount
            )

            (trackCount % 10 in 2..4 && trackCount % 100 !in 12..14) -> context.getString(
                R.string.tracks_count_few,
                trackCount
            )

            else -> context.getString(R.string.tracks_count_many, trackCount)
        }
    }
}