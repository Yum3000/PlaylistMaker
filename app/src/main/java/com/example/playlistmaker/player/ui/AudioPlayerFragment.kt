package com.example.playlistmaker.player.ui

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.PlayerTrackInfo
import com.example.playlistmaker.services.AudioPlayerService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {
    private var trackId: Int = ERROR_TRACK_ID
    private var trackUrl: String? = null
    private var trackArtist: String? = null
    private var trackTitle: String? = null

    private var shouldStartService: Boolean = false

    private val viewModel: PlayerViewModel by lazy {
        getViewModel { parametersOf(trackId) }
    }

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding get() = _binding!!

    private var playerState = PlayerState.DEFAULT

    private val playlistAdapter = PlaylistBottomAdapter(mutableListOf()) { playlist, _ ->
        viewModel.handleAddToPlaylistClick(playlist.id, playlist.title)
    }

    private var audioPlayerService: AudioPlayerService? = null
    private var isServiceConnected: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.AudioPlayerServiceBinder
            isServiceConnected = true
            viewModel.setAudioPlayerManager(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlayerService = null
            isServiceConnected = false
            viewModel.removeAudioPlayerManager()
        }
    }

    private lateinit var serviceIntent: Intent

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            bindMusicService()
        } else {
            Toast.makeText(requireContext(), "Can't bind service!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackId = requireArguments().getInt(INTENT_TRACK_KEY, ERROR_TRACK_ID)

        viewModel.getPlayerStateLiveData().observe(viewLifecycleOwner) { state ->
            trackUrl = state.trackInfo.previewUrl
            trackArtist = state.trackInfo.artistName
            trackTitle = state.trackInfo.trackName
            Log.d("AudioPlayer Fragment", "observe and get track data $trackArtist")

            shouldStartService = !trackUrl.isNullOrEmpty()

            playerState = state.playerState
            redrawPlayer(state.playerState, state.curPosition)
            redrawTrack(state.trackInfo)

            updateFavBtn(state.trackInfo.isFavourite)

            if (shouldStartService && !trackUrl.isNullOrEmpty()) {
                serviceIntent = Intent(requireContext(), AudioPlayerService::class.java).apply {
                    putExtra(INTENT_TRACK_URL, trackUrl)
                    putExtra(INTENT_TRACK_ARTIST, trackArtist)
                    putExtra(INTENT_TRACK_TITLE, trackTitle)
                }
                bindMusicService()
            }
            Log.d("AudioPlayer Fragment", "shouldStartService $shouldStartService")
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
//                == PackageManager.PERMISSION_GRANTED) {
//                bindMusicService()
//            } else {
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) // Запрос разрешения
//            }
//        } else {
//            bindMusicService()
//        }

        viewModel.getPlayerErrorToast().observe(viewLifecycleOwner) {
            showToast()
        }

        binding.playBtn.setOnClickListener {
            viewModel.handlePlayBtnClick()
        }

        binding.addFavBtn.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        binding.materialToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
            .apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        binding.addBtn.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.overlay.isVisible = true
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.overlay.isVisible = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.recyclerViewPlaylists.adapter = playlistAdapter

        binding.recyclerViewPlaylists.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        binding.createPlaylistBt.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_fragmentCreatePlaylist)
        }

        viewModel.observeBottomState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistBottomState.Content -> showContent(state.playlists)
                is PlaylistBottomState.Empty -> {
                    binding.recyclerViewPlaylists.isVisible = false
                }
            }
        }

        viewModel.observeAddTrackStatus().observe(viewLifecycleOwner) { status ->
            when (status) {
                is AddTrackStatus.Added -> {
                    val message = getString(R.string.added_to_playlist, status.playlistName)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                        .show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }

                is AddTrackStatus.Exists -> {
                    val message = getString(R.string.track_exists, status.playlistName)
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
                        .show()
                }

                is AddTrackStatus.Default -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.handleDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        unbindMusicService()
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (isServiceConnected) {
            viewModel.startForeground()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isServiceConnected) {
            viewModel.stopForeground()
        }

        if (shouldStartService) {
            Log.d("AudioPlayer Fragment", "should start service onResume")
            serviceIntent = Intent(requireContext(), AudioPlayerService::class.java).apply {
                putExtra(INTENT_TRACK_URL, trackUrl)
                putExtra(INTENT_TRACK_ARTIST, trackArtist)
                putExtra(INTENT_TRACK_TITLE, trackTitle)
            }
            bindMusicService()
        }
    }

    private fun bindMusicService() {
        requireContext().bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)
        Log.d("AudioPlayer Fragment", "bind service")
    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
        Log.d("AudioPlayer Fragment", "unbind service")
    }

    private fun redrawPlayer(state: PlayerState, curPos: String?) {
        if (curPos == null) {
            binding.currentTrackTimer.text = getString(R.string.track_timer_ph)
        } else {
            binding.currentTrackTimer.text = curPos
        }

        when (state) {
            PlayerState.PLAYING -> {
                binding.playBtn.setPlaybackBtnState(false)
            }

            PlayerState.PREPARED, PlayerState.PAUSED, PlayerState.DEFAULT -> {
                binding.playBtn.setPlaybackBtnState(true)
            }
        }
    }

    private fun redrawTrack(track: PlayerTrackInfo) {
        Glide.with(this)
            .load(track.artworkUrl100 ?: "")
            .centerInside()
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.cover_placeholder)
            .into(binding.coverTrack)

        binding.trackName.text = track.trackName ?: getString(R.string.no_data)
        binding.artistName.text = track.artistName ?: getString(R.string.no_data)
        binding.durationTV.text = track.trackTime ?: getString(R.string.no_data)
        binding.albumTV.text = track.collectionName ?: getString(R.string.no_data)
        binding.yearTV.text = track.releaseDate ?: getString(R.string.no_data)
        binding.genreTV.text = track.primaryGenreName ?: getString(R.string.no_data)
        binding.countryTV.text = track.country ?: getString(R.string.no_data)
    }

    private fun showToast() {
        Toast.makeText(
            requireContext(),
            R.string.playing_error,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateFavBtn(isFavourite: Boolean) {
        val btnIcon = if (isFavourite) {
            R.drawable.add_to_fav_btn_active
        } else {
            R.drawable.add_to_fav_btn
        }
        binding.addFavBtn.setImageResource(btnIcon)
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.recyclerViewPlaylists.isVisible = true
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlists)

        playlistAdapter.notifyDataSetChanged()
    }

    companion object {
        const val INTENT_TRACK_KEY = "track_to_player"
        const val ERROR_TRACK_ID = -1

        const val INTENT_TRACK_URL = "track_url"
        const val INTENT_TRACK_ARTIST = "track_artist"
        const val INTENT_TRACK_TITLE = "track_title"

        fun createArgs(trackId: Int?): Bundle =
            bundleOf(INTENT_TRACK_KEY to trackId)
    }
}