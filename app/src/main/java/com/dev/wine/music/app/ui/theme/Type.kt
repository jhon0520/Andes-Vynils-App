package com.dev.wine.music.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.dev.wine.music.app.R

val LibronFontFamily = FontFamily(
    Font(R.font.libron_regular, FontWeight.Normal),
    Font(R.font.libron_bold, FontWeight.Bold),
    Font(R.font.libron_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.libron_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

private val DefaultTypography = Typography()

val TypographyLibron = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = LibronFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = LibronFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = LibronFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = LibronFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = LibronFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = LibronFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = LibronFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = LibronFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = LibronFontFamily),
    bodyLarge = DefaultTypography.bodyLarge.copy(fontFamily = LibronFontFamily),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = LibronFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = LibronFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = LibronFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = LibronFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = LibronFontFamily)
)
