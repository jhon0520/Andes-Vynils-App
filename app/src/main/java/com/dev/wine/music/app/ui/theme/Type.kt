package com.dev.wine.music.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

/** Small monospaced caps for section labels ("¿CÓMO QUIERES INGRESAR?"). */
val LabelMono = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    letterSpacing = 2.4.sp,
)

/** Plain sans text from the catalog mockup: performers, search and tabs. */
val SansBody = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = 16.sp,
    lineHeight = 22.sp,
)

/** Monospaced caps for card details ("1984 · SALSA"), tighter than [LabelMono]. */
val DetailMono = LabelMono.copy(fontSize = 13.sp, letterSpacing = 1.sp)
