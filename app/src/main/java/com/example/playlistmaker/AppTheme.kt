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
    additionColor: Color
) {
    var primaryBackgroundColor by mutableStateOf(primaryBackgroundColor)
    var primaryTextColor by mutableStateOf(primaryTextColor)
    var secondaryBackgroundColor by mutableStateOf(secondaryBackgroundColor)
    var secondaryTextColor by mutableStateOf(secondaryTextColor)
    var additionColor by mutableStateOf(additionColor)

    fun updateFrom(other: CustomColors) {
        primaryBackgroundColor = other.primaryBackgroundColor
        primaryTextColor = other.primaryTextColor
        secondaryBackgroundColor = other.secondaryBackgroundColor
        secondaryTextColor = other.secondaryTextColor
        additionColor = other.additionColor
    }
}

val lightPalette = CustomColors(
    primaryBackgroundColor = White,
    primaryTextColor = Black,
    secondaryBackgroundColor = LightGrey,
    secondaryTextColor = Grey,
    additionColor = Grey
)
val darkPalette = CustomColors(
    primaryBackgroundColor = DarkGrey,
    primaryTextColor = White,
    secondaryBackgroundColor = White,
    secondaryTextColor = DarkGrey,
    additionColor = White
)

val typographyBase = AppTypography(
    title = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        color = lightPalette.primaryTextColor
    ),
    subtitle = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp,
        color = lightPalette.primaryTextColor
    ),
    button = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = lightPalette.primaryBackgroundColor
    ),
    body = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = lightPalette.primaryTextColor
    ),
    body2 = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        color = lightPalette.primaryTextColor
    ),
    body3 = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        color = lightPalette.additionColor
    ),
    desc = TextStyle(
        fontFamily = AppFont.AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = lightPalette.primaryTextColor
    )
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

class AppTypography(
    title: TextStyle,
    subtitle: TextStyle,
    button: TextStyle,
    body: TextStyle,
    body2: TextStyle,
    body3: TextStyle,
    desc: TextStyle
) {

    var title by mutableStateOf(title)
    var subtitle by mutableStateOf(subtitle)
    var button by mutableStateOf(button)
    var body by mutableStateOf(body)
    var body2 by mutableStateOf(body2)
    var body3 by mutableStateOf(body3)
    var desc by mutableStateOf(desc)

    fun copyWith(colors: CustomColors): AppTypography {
        val t = AppTypography(
            title = title.copy(color = colors.primaryTextColor),
            subtitle = subtitle.copy(color = colors.primaryTextColor),
            button = button.copy(color = colors.primaryBackgroundColor),
            body = body.copy(color = colors.primaryTextColor),
            body2 = body2.copy(color = colors.primaryTextColor),
            body3 = body3.copy(color = colors.additionColor),
            desc = desc.copy(color = colors.primaryTextColor)
        )
        return t
    }

    fun updateFrom(colors: CustomColors) {
        title = title.copy(color = colors.primaryTextColor)
        subtitle = subtitle.copy(color = colors.primaryTextColor)
        button = button.copy(color = colors.primaryBackgroundColor)
        body = body.copy(color = colors.primaryTextColor)
        body2 = body2.copy(color = colors.primaryTextColor)
        body3 = body3.copy(color = colors.additionColor)
        desc = desc.copy(color = colors.primaryTextColor)
    }
}


@Composable
fun AppTheme(
    darkThemeOn: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = remember {
        if (darkThemeOn) darkPalette else lightPalette
    }

    val typography = remember {
        typographyBase.copyWith(if (darkThemeOn) darkPalette else lightPalette)
    }

    LaunchedEffect(darkThemeOn) {
        val target = if (darkThemeOn) darkPalette else lightPalette
        colors.updateFrom(target)
        typography.updateFrom(target)
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