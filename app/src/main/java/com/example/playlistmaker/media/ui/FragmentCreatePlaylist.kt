package com.example.playlistmaker.media.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.media.presentation.PlaylistCreateState
import com.example.playlistmaker.media.presentation.PlaylistCreateViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

open class FragmentCreatePlaylist : Fragment() {

    protected open val playlistViewModel: PlaylistCreateViewModel by viewModel()

    private var _binding: FragmentCreatePlaylistBinding? = null
    protected val binding get() = _binding!!

    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val pickPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) return@registerForActivityResult

            binding.coverPlaylist.setImageURI(uri)
            val newUri = saveImageToPrivateStorage(uri)
            playlistViewModel.passArgsUri(newUri)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.observePlaylistCreateState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistCreateState -> updateForm(it.enabledBtn)
            }
        }

        binding.materialToolbar.setNavigationOnClickListener {
            if (shouldShowConfirmDialog()) {
                confirmDialog.show()
            } else {
                findNavController().navigateUp()
            }
        }

        binding.coverPlaylist.setOnClickListener {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.completion_playlist))
            .setMessage(getString(R.string.comletion_warning))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which -> }
            .setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().navigateUp()
            }

        binding.createPlaylistTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val title = p0.toString()
                playlistViewModel.handleTitleChange(title)
            }
        })

        binding.createPlaylistDesc.doOnTextChanged { p0, _, _, _ ->
            val description = p0.toString()
            playlistViewModel.handleDescChange(description)
        }

        binding.createBtn.setOnClickListener {
            playlistViewModel.createPlaylist()

            val playlistTitle = playlistViewModel.observePlaylistCreateState().value?.title
            val message = getString(R.string.playlist_created, playlistTitle)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PLAYLIST_TITLE, binding.createPlaylistTitle.text.toString())
        outState.putString(PLAYLIST_DESC, binding.createPlaylistDesc.text.toString())
        outState.putString(PLAYLIST_COVER_URI, playlistViewModel.coverUri.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            val title = savedInstanceState.getString(PLAYLIST_TITLE, "")
            val description = savedInstanceState.getString(PLAYLIST_DESC, "")
            val coverUriStr = savedInstanceState.getString(PLAYLIST_COVER_URI)

            binding.createPlaylistTitle.setText(title)
            binding.createPlaylistDesc.setText(description)

            if (coverUriStr != null) {
                val coverUri = coverUriStr.toUri()
                binding.coverPlaylist.setImageURI(coverUri)
                playlistViewModel.passArgsUri(coverUri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateForm(stateBtn: Boolean) {
        binding.createBtn.isEnabled = stateBtn
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath = File(
            requireContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES
            ), "PlaylistMakerPics"
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val playlistNum = UUID.randomUUID()
        val file = File(filePath, "$COVER_PLAYLIST_NAME $playlistNum")

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        try {
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        } finally {
            inputStream?.close()
            outputStream.close()
        }
        return file.toUri()
    }

    private fun shouldShowConfirmDialog(): Boolean {
        return if (this is FragmentModifyPlaylist) {
            false
        } else {
            playlistViewModel.observePlaylistCreateState().value?.dialogNeeded == true
        }
    }

    companion object {
        private const val COVER_PLAYLIST_NAME = "playlist_cover"
        const val PLAYLIST_TITLE = "playlist_title"
        const val PLAYLIST_DESC = "playlist_description"
        const val PLAYLIST_COVER_URI = "playlist_cover_uri"
    }
}