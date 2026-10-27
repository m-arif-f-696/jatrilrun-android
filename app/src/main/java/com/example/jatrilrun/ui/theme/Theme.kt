package com.example.jatrilrun.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val JrMaterialColorScheme = lightColorScheme(
    primary = JrOrange,
    onPrimary = JrWhite,
    primaryContainer = JrPeach,
    onPrimaryContainer = JrBlack,
    secondary = JrBlack,
    onSecondary = JrWhite,
    tertiary = JrPeach,
    onTertiary = JrBlack,
    background = JrSurface,
    onBackground = JrBlack,
    surface = JrSurface,
    onSurface = JrBlack,
    surfaceVariant = JrSand,
    onSurfaceVariant = JrTextSecondary,
    outline = JrBorder,
    outlineVariant = JrSand,
    error = JrRed,
    onError = JrWhite,
)

/**
 * Tema utama. Figma hanya punya mode terang, jadi tidak ada dark theme / dynamic color
 * (supaya warna brand tidak berubah mengikuti wallpaper).
 */
@Composable
fun JatrilrunTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalJrColors provides JrLightColors,
        LocalJrTypography provides JrTypography(),
    ) {
        MaterialTheme(
            colorScheme = JrMaterialColorScheme,
            typography = JrTypography().toMaterial(),
            shapes = JrMaterialShapes,
            content = content,
        )
    }
}

/** Akses token: JatrilrunTheme.colors.statusSuccess, JatrilrunTheme.typography.title, dst. */
object JatrilrunTheme {
    val colors: JrColors
        @Composable get() = LocalJrColors.current
    val typography: JrTypography
        @Composable get() = LocalJrTypography.current
}