package com.example.playlistmaker.player.ui

import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistBottomSheetViewBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistBottomViewHolder(
    private val binding: PlaylistBottomSheetViewBinding
): RecyclerView.ViewHolder(binding.root) {
    private val cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f,
        itemView.resources.displayMetrics).toInt()

    fun bind(playlist: Playlist) {
        binding.listTitle.text = playlist.title ?: ""
        binding.listCount.text = playlist.tracksCount.toString()
        Glide.with(itemView)
            .load(playlist.coverPath ?: "")
            .placeholder(R.drawable.cover_placeholder)
            .transform(RoundedCorners(cornerRadius))
            .centerCrop()
            .into(binding.listCover)
    }
}