package com.example.jatrilrun.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Raw palette — sumber: Figma "01 — Design System" / Foundations (Warna semantik)
// ---------------------------------------------------------------------------
val JrOrange = Color(0xFFD83D00)        // Primary  (jr-color-action-primary)
val JrBlack = Color(0xFF171717)         // Secondary (jr-color-background-inverse / text-primary)
val JrPeach = Color(0xFFF3CEB7)         // Accent   (jr-color-secondary-accent)
val JrSand = Color(0xFFE8E5E2)          // Neutral  (jr-color-background-subtle)
val JrGreen = Color(0xFF3E7652)         // Success  (jr-color-status-success)
val JrAmber = Color(0xFF665015)         // Warning  (jr-color-status-warning)
val JrRed = Color(0xFFB52424)           // Error    (jr-color-status-error)

val JrSurface = Color(0xFFF2F2F2)       // jr-color-background-primary
val JrTextSecondary = Color(0xFF5B5551) // jr-color-text-secondary
val JrTextBrand = Color(0xFFA62F00)     // jr-color-text-brand (oranye gelap, aman untuk teks)
val JrWhite = Color(0xFFFFFFFF)

// ASUMSI: tidak ada token border di Figma yang terbaca. Sesuaikan jika ada.
val JrBorder = Color(0xFFCFC9C4)

/**
 * Token semantik Jatrilrun. Pakai lewat [JatrilrunTheme.colors].
 * Nama mengikuti variabel Figma `--jr-color-*`.
 */
@Immutable
data class JrColors(
    val backgroundPrimary: Color,
    val backgroundSubtle: Color,
    val backgroundInverse: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textBrand: Color,
    val textOnPrimary: Color,
    val textOnInverse: Color,
    val actionPrimary: Color,
    val secondaryAccent: Color,
    val statusSuccess: Color,
    val statusWarning: Color,
    val statusError: Color,
    val border: Color,
)

val JrLightColors = JrColors(
    backgroundPrimary = JrSurface,
    backgroundSubtle = JrSand,
    backgroundInverse = JrBlack,
    textPrimary = JrBlack,
    textSecondary = JrTextSecondary,
    textBrand = JrTextBrand,
    textOnPrimary = JrWhite,   // putih di atas #D83D00 = 4,58:1
    textOnInverse = JrWhite,
    actionPrimary = JrOrange,
    secondaryAccent = JrPeach,
    statusSuccess = JrGreen,
    statusWarning = JrAmber,
    statusError = JrRed,
    border = JrBorder,
)

val LocalJrColors = staticCompositionLocalOf { JrLightColors }