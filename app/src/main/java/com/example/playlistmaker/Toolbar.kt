package com.example.playlistmaker

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun Toolbar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = AppTheme.colors.primaryTextColor,
                style = AppTheme.typography.title,
            )
        },
        backgroundColor = AppTheme.colors.primaryBackgroundColor
    )
}