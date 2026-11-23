package com.example.jatrilrun.ui.screens.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.ui.screens.event.EventAction
import com.example.jatrilrun.ui.screens.event.EventBody
import com.example.jatrilrun.ui.screens.event.EventCardWhite
import com.example.jatrilrun.ui.screens.event.EventLink
import com.example.jatrilrun.ui.screens.event.EventText
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrShapes

/**
 * Figma "HF / Konfirmasi keluar pendaftaran": bottom sheet di atas layar Data peserta.
 * "Lanjutkan mendaftar" = tutup sheet; "Keluar ke Beranda" = keluar alur (draft tetap ada di RegistrationViewModel).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExitRegistrationSheet(onContinue: () -> Unit, onExit: () -> Unit) {
    val colors = JatrilrunTheme.colors
    ModalBottomSheet(
        onDismissRequest = onContinue, // tap scrim / swipe turun / back = tetap mendaftar
        containerColor = EventCardWhite,
        shape = JrShapes.BottomSheet,
        dragHandle = null
    ) {
        Column(
            Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Handle rata kiri sesuai Figma (bukan di tengah seperti drag handle bawaan).
            Box(
                Modifier.width(48.dp).height(4.dp)
                    .background(colors.textSecondary.copy(alpha = 0.5f), JrShapes.Pill)
            )
            EventText("Tinggalkan pendaftaran?", JatrilrunTheme.typography.heading)
            EventBody("Data tetap tersimpan. Anda dapat melanjutkan sebelum kuota habis.")
            EventAction("Lanjutkan mendaftar", onContinue)
            EventLink("Keluar ke Beranda", onExit, Modifier.fillMaxWidth())
        }
    }
}

/**
 * Membungkus layar langkah pertama alur daftar. Tombol back (bar atas maupun sistem) membuka sheet
 * alih-alih langsung keluar. [content] menerima `requestExit` untuk dipasang di onBack layar.
 */
@Composable
internal fun RegistrationExitGuard(
    onExit: () -> Unit,
    content: @Composable (requestExit: () -> Unit) -> Unit
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = !showSheet) { showSheet = true }
    content { showSheet = true }
    if (showSheet) {
        ExitRegistrationSheet(
            onContinue = { showSheet = false },
            onExit = {
                showSheet = false
                onExit()
            }
        )
    }
}
