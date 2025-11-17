package com.example.playlistmaker.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.AppTheme

@Composable
fun PlaylistMakerButton(title: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(54.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.colors.primaryTextColor,
            contentColor = AppTheme.colors.secondaryTextColor
        )
    ) {
        Text(text = title,
            style = AppTheme.typography.button,
            fontWeight = FontWeight(500)
        )
    }
}