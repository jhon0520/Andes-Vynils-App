package com.dev.wine.music.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = DarkBackground,
)

// Warm palette from the mockups: ink on paper with terracotta accents, no Material purple.
private val LightColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = SleeveText,
    secondary = RecordGreen,
    onSecondary = SleeveText,
    tertiary = SleeveBrown,
    onTertiary = SleeveText,
    background = LightBackground,
    onBackground = RecordBlack,
    surface = LightBackground,
    onSurface = RecordBlack,
    surfaceVariant = SleeveSand,
    onSurfaceVariant = NavBarInactive,
    surfaceContainer = NavBarBackground,
    outlineVariant = NavBarDivider,
)

@Composable
fun WineMusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TypographyLibron,
        content = content
    )
}