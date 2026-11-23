package com.example.jatrilrun.ui.screens.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.ui.theme.JatrilrunTheme

@Composable
fun SummaryScreen(
    onBack: () -> Unit,
    onEditParticipant: () -> Unit,
    onContinue: () -> Unit,
    summary: CheckoutSummary = CheckoutSummary.Sample
) {
    val colors = JatrilrunTheme.colors

    EventScreen("Ringkasan", onBack, bottomBar = {
        EventFooter("Pilih metode pembayaran", onContinue)
    }) {
        RegistrationStepper(step = 2, label = "2 dari 4 · Ringkasan")
        EventTitle("Periksa sebelum bayar.")

        RegistrationSummaryCard(
            summary,
            highlight = "${summary.category} · Pendaftaran ${rupiah(summary.registrationFee)}"
        )

        EventHeading("Rincian biaya")
        CostBreakdown(summary)

        EventLink("Ubah data peserta", onEditParticipant, Modifier.fillMaxWidth().heightIn(min = 52.dp))

        EventPanel("Kontak darurat") {
            HorizontalDivider(color = colors.border)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EventIcon(R.drawable.user)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    EventCaption("Kontak darurat")
                    EventText("${summary.emergencyName} · ${summary.emergencyPhone}")
                }
            }
        }

        EventNotice(
            "Periksa nama BIB dan jersey",
            "${summary.bibName} · Ukuran ${summary.jersey}. Harga sudah mencakup race kit. " +
                "Biaya layanan ditampilkan di atas."
        )
    }
}
