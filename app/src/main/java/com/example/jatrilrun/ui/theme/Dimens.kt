package com.example.jatrilrun.ui.theme

import androidx.compose.ui.unit.dp

/** Spacing scale: 4, 8, 12, 16, 20, 24, 32, 40, 48, 64 (token --jr-spacing-XX). */
object JrSpacing {
    val None = 0.dp
    val S4 = 4.dp
    val S8 = 8.dp
    val S12 = 12.dp
    val S16 = 16.dp
    val S20 = 20.dp
    val S24 = 24.dp
    val S32 = 32.dp
    val S40 = 40.dp
    val S48 = 48.dp
    val S64 = 64.dp
}

/** Layout & ukuran kontrol. Mobile frame 390 x 844, 4 kolom. */
object JrDimens {
    val ScreenMargin = 20.dp
    val Gutter = 12.dp
    val ControlHeight = 52.dp      // tombol, text field, search
    val MinTouchTarget = 44.dp     // semua area tap minimal 44 x 44
    val IconSize = 24.dp           // stroke 1.8px
    val BorderWidth = 1.dp
    val BorderSelectedWidth = 2.dp
    val FocusRingWidth = 2.dp
    val FocusRingOffset = 2.dp
}


/** Elevation: card untuk kartu, raised untuk CTA, overlay untuk modal/bottom sheet. */
object JrElevation {
    // ASUMSI nilai; Figma hanya menyebut nama level-nya.
    val Card = 2.dp
    val Raised = 4.dp
    val Overlay = 8.dp
}