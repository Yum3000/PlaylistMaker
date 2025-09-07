package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistBottomSheetViewBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistBottomAdapter(
    var playlists: MutableList<Playlist>,
    private val onPlaylistClick: (Playlist, PlaylistBottomAdapter) -> Unit
): RecyclerView.Adapter<PlaylistBottomViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistBottomViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistBottomViewHolder(PlaylistBottomSheetViewBinding.inflate(
            layoutInspector, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: PlaylistBottomViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            onPlaylistClick(playlists[position], this)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }
}