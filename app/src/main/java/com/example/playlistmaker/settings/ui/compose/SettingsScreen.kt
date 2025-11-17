package com.example.playlistmaker.settings.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.Blue
import com.example.playlistmaker.Grey
import com.example.playlistmaker.LightBlue
import com.example.playlistmaker.LightGrey
import com.example.playlistmaker.R
import com.example.playlistmaker.components.Toolbar
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    onThemeChange: (isDark: Boolean) -> Unit,
    viewModel: SettingsViewModel = koinViewModel()) {
    val settingsThemeDark by viewModel.getSettingsThemeDarkLiveData().observeAsState()

    val darkTheme = stringResource(R.string.dark_theme)
    val shareBtn = stringResource(R.string.share_bt)
    val supportBtn = stringResource(R.string.support_bt)
    val agreement = stringResource(R.string.user_agreement)

    val practicumOffer = stringResource(R.string.practicum_offer)
    val supportSubject = stringResource(R.string.support_subject)
    val supportBody = stringResource(R.string.support_body)
    val supportEmail = stringResource(R.string.support_email)

    val shareMsg = stringResource(R.string.share_app_msg)

    Scaffold(topBar = {
        Toolbar(title = stringResource(R.string.settings_bt))
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppTheme.colors.primaryBackgroundColor)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SettingsButtonWithSwitch(darkTheme, settingsThemeDark ?: false) { darkThemeOn ->
                viewModel.switchTheme(darkThemeOn)
                onThemeChange(darkThemeOn)
            }

            SettingsButton(shareBtn, R.drawable.share_icon) {
                viewModel.shareApp(shareMsg)
            }

            SettingsButton(supportBtn, R.drawable.support_icon) {
                viewModel.writeToSupport(
                    supportSubject, supportBody, supportEmail
                )
            }

            SettingsButton(agreement, R.drawable.arrow_forward_icon) {
                viewModel.openUserAgreement(practicumOffer)
            }
        }
    }

}

@Composable
fun SettingsButtonWithSwitch(title: String, enabled: Boolean, onClick: (Boolean) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp),
            style = AppTheme.typography.body
        )
        Switch(
            checked = enabled,
            modifier = Modifier
                .padding(end = 6.dp),
            onCheckedChange = { onClick(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Blue,
                checkedTrackColor = LightBlue,
                uncheckedThumbColor = Grey,
                uncheckedTrackColor = LightGrey
            )
        )
    }
}

@Composable
fun SettingsButton(title: String, icon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            style = AppTheme.typography.body
        )

        Icon(
            modifier = Modifier.padding(end = 18.dp),
            painter = painterResource(icon),
            contentDescription = null,
            tint = LightGrey
        )
    }
}

