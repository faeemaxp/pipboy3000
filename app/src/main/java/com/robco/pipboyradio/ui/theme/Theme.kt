package com.robco.pipboyradio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun getPipBoyColorScheme(theme: String) = when (theme.uppercase()) {
    "AMBER" -> darkColorScheme(
        primary = Color(0xFFFFB000),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF6B4500),
        onPrimaryContainer = Color(0xFFFFE0B2),
        secondary = Color(0xFFC67D00),
        onSecondary = Color.Black,
        background = Color(0xFF0F0B06),
        onBackground = Color(0xFFFFB000),
        surface = Color(0xFF19130A),
        onSurface = Color(0xFFFFB000),
        surfaceVariant = Color(0xFF241B0E),
        onSurfaceVariant = Color(0xFFC67D00),
        outline = Color(0xFFC67D00),
        error = AccentRed,
        onError = Color.Black
    )
    "CYAN" -> darkColorScheme(
        primary = Color(0xFF00E5FF),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF004D5A),
        onPrimaryContainer = Color(0xFFB2EBF2),
        secondary = Color(0xFF0091AA),
        onSecondary = Color.Black,
        background = Color(0xFF050E12),
        onBackground = Color(0xFF00E5FF),
        surface = Color(0xFF0A171D),
        onSurface = Color(0xFF00E5FF),
        surfaceVariant = Color(0xFF0F222B),
        onSurfaceVariant = Color(0xFF0091AA),
        outline = Color(0xFF0091AA),
        error = AccentRed,
        onError = Color.Black
    )
    "WHITE" -> darkColorScheme(
        primary = Color(0xFFE6E6E6),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF424242),
        onPrimaryContainer = Color(0xFFFFFFFF),
        secondary = Color(0xFFAAAAAA),
        onSecondary = Color.Black,
        background = Color(0xFF0D0D0D),
        onBackground = Color(0xFFE6E6E6),
        surface = Color(0xFF171717),
        onSurface = Color(0xFFE6E6E6),
        surfaceVariant = Color(0xFF222222),
        onSurfaceVariant = Color(0xFFAAAAAA),
        outline = Color(0xFFAAAAAA),
        error = AccentRed,
        onError = Color.Black
    )
    else -> darkColorScheme(
        primary = PhosphorPrimary,
        onPrimary = Color.Black,
        primaryContainer = PhosphorDim,
        onPrimaryContainer = PhosphorBright,
        secondary = PhosphorSecondary,
        onSecondary = Color.Black,
        background = ScreenBackground,
        onBackground = PhosphorPrimary,
        surface = SurfaceCard,
        onSurface = PhosphorPrimary,
        surfaceVariant = SurfaceCardElevated,
        onSurfaceVariant = PhosphorSecondary,
        outline = SurfaceBorder,
        error = AccentRed,
        onError = Color.Black
    )
}

@Composable
fun PipBoyTheme(
    colorTheme: String = "GREEN",
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = getPipBoyColorScheme(colorTheme),
        typography = PipBoyTypography,
        content = content
    )
}
