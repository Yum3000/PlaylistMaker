package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.domain.models.PlaylistDetails
import com.example.playlistmaker.media.presentation.PlaylistState
import com.example.playlistmaker.media.presentation.PlaylistViewModel
import com.example.playlistmaker.player.ui.AudioPlayerFragment
import com.example.playlistmaker.search.domain.models.ListTrackInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment() : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private var playlistId: Int = ERROR_PLAYLIST_ID

    private val playlistViewModel: PlaylistViewModel by lazy {
        getViewModel { parametersOf(playlistId) }
    }

    lateinit var deleteTrackDialog: MaterialAlertDialogBuilder
    lateinit var deletePlaylistDialog: MaterialAlertDialogBuilder

    val playlistDetailAdapter = PlaylistDetailAdapter(
        onTrackClick = { listTrackInfo ->
            playlistViewModel.handleTrackClick(listTrackInfo)
        },
        onTrackLongClick = { listTrackInfo ->
            openDeleteDialog(listTrackInfo)
            true
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = requireArguments().getInt(INTENT_PLAYLIST_KEY, ERROR_PLAYLIST_ID)

        binding.recyclerViewTracks.adapter = playlistDetailAdapter

        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        playlistViewModel.updatePlaylistDetails()

        deleteTrackDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.dialog_delete_track))
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, which ->
                dialog.dismiss()
            }


        playlistViewModel.observePlaylistState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Content -> {
                    showContent(state.playlistDetails)
                    loadPlaylistDetails(state.playlistDetails)
                }
            }
        }

        playlistViewModel.observePlaylistToDelete().observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        playlistViewModel.observeToastMsgLiveData().observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        val bottomSheetTracks = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val bottomSheetMenu = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.recyclerViewTracks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        playlistViewModel.getTrackToOpenPlayer().observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }

        binding.iconMenu.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.overlay.isVisible = true
        }

        bottomSheetMenu.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        binding.iconShare.isEnabled = true
                        binding.iconMenu.isEnabled = true
                    }

                    else -> {
                        binding.iconShare.isEnabled = false
                        binding.iconMenu.isEnabled = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        binding.iconShare.setOnClickListener {
            playlistViewModel.sharePlaylist()
        }

        binding.shareBtn.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
            playlistViewModel.sharePlaylist()
        }

        binding.deletePlaylistBtn.setOnClickListener {
            bottomSheetMenu.state = BottomSheetBehavior.STATE_HIDDEN
            deletePlaylistDialog.show()
        }

        binding.editPlaylistBtn.setOnClickListener {
            openModifyPlaylistScreen(playlistId)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(playlist: PlaylistDetails) {
        Glide.with(this)
            .load(playlist.coverPath)
            .placeholder(R.drawable.cover_placeholder)
            .into(binding.coverPlaylist)

        binding.playlistTitle.text = playlist.title
        binding.playlistDesc.text = playlist.description

        binding.tracksCount.text = playlist.tracksCount
        binding.tracksDuration.text = playlist.tracksDuration

        if (playlist.tracksList.isEmpty()) {
            binding.recyclerViewTracks.isVisible = false
            binding.emptyPlaylistMsg.isVisible = true
        } else {
            binding.recyclerViewTracks.isVisible = true
            playlistDetailAdapter.updateTracks(playlist.tracksList)
        }
    }

    private fun openPlayer(track: ListTrackInfo) {
        // !!!!!!
        val bundle = AudioPlayerFragment.createArgs(
            track.trackId,
            "",
            "",
            "")
        findNavController().navigate(R.id.action_playlistFragment_to_audioPlayerFragment, bundle)
    }

    private fun openDeleteDialog(track: ListTrackInfo) {
        deleteTrackDialog.setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->
            playlistViewModel.deleteTrack(track.trackId)
        }.show()
    }

    private fun loadPlaylistDetails(playlistDetails: PlaylistDetails) {

        binding.menuBottomSheet.isVisible = true

        val cornerRadius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2f,
            resources.displayMetrics
        ).toInt()
        Glide.with(this)
            .load(playlistDetails.coverPath)
            .placeholder(R.drawable.cover_placeholder)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .into(binding.currentTrack.listCover)

        binding.currentTrack.listTitle.text = playlistDetails.title
        binding.currentTrack.listCount.text = playlistDetails.tracksCount
        binding.currentTrack.listCount.setTextColor(ContextCompat.getColor(requireContext(),R.color.cur_track_playlist_menu))

        deletePlaylistDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.dialog_delete_playlist, playlistDetails.title))
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_yes)) { dialog, which ->
                playlistViewModel.deletePlaylist()
            }
    }

    private fun openModifyPlaylistScreen(playlistId: Int) {
        val bundle = FragmentModifyPlaylist.createArgs(playlistId)
        findNavController().navigate(R.id.action_playlistFragment_to_fragmentModifyPlaylist, bundle)
    }

    companion object {
        const val ERROR_PLAYLIST_ID = -1
        const val INTENT_PLAYLIST_KEY = "playlist_content"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(INTENT_PLAYLIST_KEY to playlistId)
    }
}