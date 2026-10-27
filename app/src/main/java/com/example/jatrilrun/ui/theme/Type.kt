package com.example.jatrilrun.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.jatrilrun.R

// Taruh file font statis Plus Jakarta Sans di res/font/ (huruf kecil, underscore):
//   plus_jakarta_sans_regular.ttf, _medium.ttf, _semibold.ttf, _bold.ttf, _extrabold.ttf
val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

private fun jr(weight: FontWeight, size: Int, line: Int) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = 0.sp,
)

/** Style bernama persis seperti di Figma (Jatrilrun/display, /title, dst). */
@Immutable
data class JrTypography(
    val display: TextStyle = jr(FontWeight.ExtraBold, 40, 48),     // Hero / judul besar
    val title: TextStyle = jr(FontWeight.Bold, 28, 36),            // Judul layar
    val heading: TextStyle = jr(FontWeight.Bold, 22, 30),          // Judul bagian
    val subheading: TextStyle = jr(FontWeight.Bold, 18, 26),       // Judul kartu
    val body: TextStyle = jr(FontWeight.Normal, 16, 24),           // Konten utama
    val bodySmall: TextStyle = jr(FontWeight.Normal, 14, 22),      // Konten pendukung
    val label: TextStyle = jr(FontWeight.SemiBold, 14, 20),        // Label & aksi
    val caption: TextStyle = jr(FontWeight.Medium, 12, 18),        // Metadata
)

val LocalJrTypography = staticCompositionLocalOf { JrTypography() }

/** Mapping ke Material3 supaya komponen bawaan (Text, Button, dll) ikut gaya Jatrilrun. */
fun JrTypography.toMaterial() = Typography(
    displayMedium = display,
    headlineLarge = title,
    headlineMedium = heading,
    titleLarge = subheading,
    titleMedium = label,
    bodyLarge = body,
    bodyMedium = bodySmall,
    bodySmall = caption,
    labelLarge = label,
    labelMedium = label,
    labelSmall = caption,
)