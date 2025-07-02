package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.getSettingsThemeDarkLiveData().observe(this) { isDark ->
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