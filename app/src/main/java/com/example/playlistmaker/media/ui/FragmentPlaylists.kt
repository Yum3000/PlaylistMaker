package com.example.playlistmaker.media.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.presentation.MediaScreenPlaylistsState
import com.example.playlistmaker.media.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists: Fragment() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val playlistsAdapter = PlaylistAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val checkPerm = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (checkPerm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    requireContext(), R.string.permissions_granted, Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(), R.string.permissions_denied, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = playlistsAdapter

        playlistsViewModel.observeStatePlaylists().observe(viewLifecycleOwner){
            when(it){
                is MediaScreenPlaylistsState.Content -> showContent(it.playlists)
                is MediaScreenPlaylistsState.Empty -> showEmpty()
            }
        }

        playlistsViewModel.handleViewCreated()

        binding.createPlaylistBt.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_fragmentCreatePlaylist)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContent(playlists: List<Playlist>){
        binding.messageView.root.isVisible = false
        binding.recyclerView.isVisible = true
        playlistsAdapter.playlists.clear()
        playlistsAdapter.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
    }

    private fun showEmpty(){
        binding.messageView.root.isVisible = true
        binding.messageView.placeholderMessage.text = requireActivity().getString(R.string.no_playlists)

        val imageResource = getPlaceholderImageResource()
        binding.messageView.placeholderImage.setImageResource(imageResource)
    }

    private fun getPlaceholderImageResource(): Int {
        return if (isNightMode()) {
            R.drawable.no_results_icon_dark
        } else {
            R.drawable.no_results_icon_light
        }
    }

    private fun isNightMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        fun newInstance() : FragmentPlaylists = FragmentPlaylists()
    }
}