package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.presentation.PlaylistModifyViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FragmentModifyPlaylist(): FragmentCreatePlaylist() {

    private var playlistId: Int = ERROR_ID
    override lateinit var playlistViewModel: PlaylistModifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playlistId = requireArguments().getInt(INTENT_EDIT_PLAYLIST, ERROR_ID)

        playlistViewModel = getViewModel { parametersOf(playlistId) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel = getViewModel { parametersOf(playlistId) }

        binding.createBtn.setText(R.string.save)
        binding.screenTitle.setText(R.string.edit)

        binding.materialToolbar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createBtn.setOnClickListener {
            playlistViewModel.saveUpdates(playlistId)
        }

        playlistViewModel.observeInitPlaylistState().observe(viewLifecycleOwner) { playlist ->
            if (playlist != null) {
                loadPlaylistDetails(playlist)
            }
        }
    }

    private fun loadPlaylistDetails(playlist: Playlist) {
        binding.createPlaylistTitle.setText(playlist.title)
        binding.createPlaylistDesc.setText(playlist.description)
        binding.coverPlaylist.setImageURI(playlist.coverPath)
    }

    companion object {
        const val ERROR_ID = -1
        const val INTENT_EDIT_PLAYLIST = "edit_playlist"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(INTENT_EDIT_PLAYLIST to playlistId)
    }
}