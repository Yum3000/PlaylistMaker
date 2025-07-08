package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSettingsThemeDarkLiveData().observe(viewLifecycleOwner) { isDark ->
            binding.switchTheme.isChecked = isDark
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
        }

        binding.shareTextview.setOnClickListener{
            viewModel.shareApp(getString(R.string.share_app_msg))
        }

        binding.supportTextview.setOnClickListener {
            viewModel.writeToSupport(
                getString(R.string.support_subject),
                getString(R.string.support_body),
                getString(R.string.support_email)
            )
        }

        binding.userAgreementTextview.setOnClickListener {
            viewModel.openUserAgreement(getString(R.string.practicum_offer))
        }
    }
}