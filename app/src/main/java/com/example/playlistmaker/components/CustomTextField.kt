package com.example.playlistmaker.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.AppTheme
import com.example.playlistmaker.R

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String,
    onTextChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit
) {
    BasicTextField(
        modifier = modifier
            .background(
                color = AppTheme.colors.secondaryBackgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .onFocusChanged {
                onFocusChanged(it.isFocused)
            },

        value = text,
        onValueChange = {
            onTextChanged(it)
        },
        singleLine = true,
        cursorBrush = SolidColor(colorResource(R.color.blue)),
        textStyle = AppTheme.typography.body.copy(color = colorResource(R.color.dark_grey)),
        decorationBox = { innerTextField ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = AppTheme.typography.body,
                            color = AppTheme.colors.secondaryTextColor
                        )
                    }
                    innerTextField()
                }

                if (trailingIcon != null && text.isNotEmpty()) trailingIcon()
            }
        }
    )
}