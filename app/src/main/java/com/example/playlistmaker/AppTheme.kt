package com.example.playlistmaker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
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

data class ColorPalette(
    val primaryBackgroundColor: Color,
    val primaryTextColor: Color,
    val secondaryBackgroundColor: Color,
    val secondaryTextColor: Color,
)

val lightPalette = ColorPalette(
    primaryBackgroundColor = White,
    primaryTextColor = Black,
    secondaryBackgroundColor = LightGrey,
    secondaryTextColor = Black
)
val darkPalette = ColorPalette(
    primaryBackgroundColor = DarkGrey,
    primaryTextColor = White,
    secondaryBackgroundColor = White,
    secondaryTextColor = White
)

val LocalColors = staticCompositionLocalOf<ColorPalette> {
    //error("Colors composition error")
    lightPalette
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

object AppTheme {
    val colors: ColorPalette
        @Composable
        get() = LocalColors.current

    val typography: AppTypography
        @Composable
        get() = LocalTypography.current
}

@Composable
fun AppTheme(
    darkThemeOn: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (!darkThemeOn) lightPalette
    else darkPalette

    val typography = AppTypography(
        title = TextStyle(
            fontFamily = AppFont.AppFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            color = LocalColors.current.primaryTextColor
        ),
        button = TextStyle(
            fontFamily = AppFont.AppFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = LocalColors.current.primaryBackgroundColor
        ),
        body = TextStyle(
            fontFamily = AppFont.AppFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = LocalColors.current.primaryTextColor
        )
    )

    //val colorPalette = remember { colors }
    //val appTypography = remember { typography }

    CompositionLocalProvider(
        LocalColors.provides(colors),
        LocalTypography.provides(typography),
        content = content
    )

}



class CustomColors(
    content: Color,
) {
    var content by mutableStateOf(content)
    private set

    fun update(colors: CustomColors) {
        content = colors.content
    }
}

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        content = Color.Unspecified,
    )
}

@Composable
fun CustomTheme(
    content: @Composable () -> Unit
) {
    val customColors = CustomColors(
        content = Color(0xFFDD0D3C),
    )

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        content = content
    )
}

object CustomTheme {
    val colors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}