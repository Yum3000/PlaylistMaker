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
import kotlin.random.Random

class FragmentCreatePlaylist: Fragment() {

    private val playlistCreateViewModel: PlaylistCreateViewModel by viewModel()

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val pickPhoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.coverPlaylist.setImageURI(uri)
            val newUri = saveImageToPrivateStorage(uri)
            playlistCreateViewModel.passArgsUri(newUri)
        }
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

        playlistCreateViewModel.observePlaylistCreateState().observe(viewLifecycleOwner) {
            when (it) {
                is PlaylistCreateState -> updateForm(it.enabledBtn)
            }
        }

        binding.materialToolbar.setNavigationOnClickListener {

            if (playlistCreateViewModel.observePlaylistCreateState().value?.dialogNeeded == true) {
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
            .setNeutralButton (getString(R.string.cancel)){ dialog, which -> }
            .setPositiveButton(getString(R.string.finish)){ dialog, which ->
                findNavController().navigateUp()
            }

        binding.createPlaylistTitle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged (p0: Editable?) { }

            override fun beforeTextChanged (p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged (p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val title = p0.toString()
                playlistCreateViewModel.handleTitleChange(title)
            }
        })

        binding.createPlaylistDesc.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged (p0: Editable?) { }

            override fun beforeTextChanged (p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged (p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val description = p0.toString()
                playlistCreateViewModel.handleDescChange(description)
            }
        })

        binding.createBtn.setOnClickListener {
            playlistCreateViewModel.createPlaylist()

            val playlistTitle = playlistCreateViewModel.observePlaylistCreateState().value?.title
            val message = getString(R.string.playlist_created, playlistTitle)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun updateForm(stateBtn: Boolean) {
        binding.createBtn.isEnabled = stateBtn
    }

    private fun saveImageToPrivateStorage(uri: Uri): Uri {
        val filePath = File(requireContext().getExternalFilesDir(
            Environment.DIRECTORY_PICTURES), "PlaylistMakerPics")

        if (!filePath.exists()){
            filePath.mkdirs()
        }

        var playlistNum: Int
        var file: File
        do {
            playlistNum = generateRandomNum(1, 100)
            file = File(filePath, "$COVER_PLAYLIST_NAME $playlistNum")
        } while (file.exists())

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        inputStream?.close()
        outputStream.close()
        return file.toUri()
    }

    private fun generateRandomNum(min: Int, max: Int): Int {
        return Random.nextInt(min, max + 1)
    }

    companion object {
        private const val COVER_PLAYLIST_NAME = "playlist_cover"
    }
}