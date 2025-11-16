package com.example.playlistmaker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class CustomColors(
    primaryBackgroundColor: Color,
    primaryTextColor: Color,
    secondaryBackgroundColor: Color,
    secondaryTextColor: Color,
) {
    var primaryBackgroundColor by mutableStateOf(primaryBackgroundColor)
    var primaryTextColor by mutableStateOf(primaryTextColor)
    var secondaryBackgroundColor by mutableStateOf(secondaryBackgroundColor)
    var secondaryTextColor by mutableStateOf(secondaryTextColor)

    fun updateFrom(other: CustomColors) {
        primaryBackgroundColor = other.primaryBackgroundColor
        primaryTextColor = other.primaryTextColor
        secondaryBackgroundColor = other.secondaryBackgroundColor
        secondaryTextColor = other.secondaryTextColor
    }
}

val lightPalette = CustomColors(
    primaryBackgroundColor = White,
    primaryTextColor = Black,
    secondaryBackgroundColor = LightGrey,
    secondaryTextColor = Black
)
val darkPalette = CustomColors(
    primaryBackgroundColor = DarkGrey,
    primaryTextColor = White,
    secondaryBackgroundColor = White,
    secondaryTextColor = White
)

val LocalColors = staticCompositionLocalOf<CustomColors> {
    error("Colors composition error")
}

val LocalTypography = staticCompositionLocalOf<AppTypography> {
    error("Typography composition error")
}

object AppFont {
    val AppFontFamily = FontFamily(
        Font(R.font.ys_display_regular, FontWeight.Normal),
        Font(R.font.ys_display_medium, FontWeight.Medium),
        Font(R.font.ys_display_bold, FontWeight.Bold)
    )
}

data class AppTypography(
    val title: TextStyle,
    val button: TextStyle,
    val body: TextStyle
)


@Composable
fun AppTheme(
    darkThemeOn: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = remember {
        if (darkThemeOn) darkPalette else lightPalette
    }

    val typography = AppTypography(
        title = TextStyle(
            fontFamily = AppFont.AppFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = colors.primaryTextColor
        ),
        button = TextStyle(
            fontFamily = AppFont.AppFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = colors.primaryBackgroundColor
        ),
        body = TextStyle(
            fontFamily = AppFont.AppFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = colors.primaryTextColor
        )
    )

    LaunchedEffect(darkThemeOn) {
        val target = if (darkThemeOn) darkPalette else lightPalette
        colors.updateFrom(target)
    }

    CompositionLocalProvider(
        LocalColors.provides(colors),
        LocalTypography.provides(typography),
        content = content
    )

}

object AppTheme {
    val colors: CustomColors
        @Composable
        get() = LocalColors.current

    val typography: AppTypography
        @Composable
        get() = LocalTypography.current
}