package com.example.jatrilrun.ui.screens.checkout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun ConsentScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    var readTerms by rememberSaveable { mutableStateOf(false) }
    var dataIsTrue by rememberSaveable { mutableStateOf(false) }
    val agreed = readTerms && dataIsTrue

    EventScreen("Syarat & waiver", onBack, bottomBar = {
        EventFooter(
            label = if (agreed) "Setuju & lihat ringkasan" else "Setujui untuk lanjut",
            onClick = onContinue,
            enabled = agreed
        )
    }) {
        RegistrationStepper(step = 1, label = "1 dari 4 · Persetujuan")
        EventTitle("Daftar dengan\nmemahami risikonya.")
        EventPanel("Ketentuan pendaftaran") {
            EventBody(
                "Usia minimum 18 tahun untuk 30K. Data peserta harus sesuai identitas. " +
                    "Biaya tidak dapat dikembalikan; pemindahan nama dibuka sampai 1 November 2026."
            )
        }
        EventPanel("Pernyataan peserta") {
            EventBody(
                "Saya memahami risiko trail run, termasuk cedera dan cuaca ekstrem. " +
                    "Saya menyatakan kondisi fisik saya siap, akan membawa perlengkapan wajib, " +
                    "mematuhi marshal, dan menerima pertolongan medis bila diperlukan."
            )
            ConsentCheckbox(readTerms, "Saya telah membaca syarat event dan waiver.") { readTerms = it }
            ConsentCheckbox(dataIsTrue, "Saya menyatakan data dan kondisi kesehatan saya benar.") { dataIsTrue = it }
            EventCaption("Anda perlu menyetujui kedua pernyataan untuk melanjutkan.")
        }
    }
}
