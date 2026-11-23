package com.example.jatrilrun.ui.screens.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.screens.checkout.CheckoutSummary
import com.example.jatrilrun.ui.screens.checkout.ErrorNotice
import com.example.jatrilrun.ui.screens.checkout.ErrorTint
import com.example.jatrilrun.ui.screens.checkout.EventStatusBadge
import com.example.jatrilrun.ui.screens.checkout.RegistrationSummaryCard
import com.example.jatrilrun.ui.screens.checkout.StatusTone
import com.example.jatrilrun.ui.screens.checkout.SuccessTint
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.ui.theme.JatrilrunTheme

@Composable
fun PaymentSuccessScreen(
    onBack: () -> Unit,
    onViewRacePass: () -> Unit,
    summary: CheckoutSummary = CheckoutSummary.Sample,
    reference: String = "JTR-PAY-261006-03048", // DEMO: referensi dari backend
    paidAt: String = "6 Oktober 2026 · 14.32 WIB" // DEMO: waktu bayar dari backend
) {
    val colors = JatrilrunTheme.colors

    EventScreen("Pembayaran berhasil", onBack, bottomBar = {
        EventFooter("Lihat race pass", onViewRacePass)
    }) {
        EventCard(color = SuccessTint) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EventIcon(R.drawable.check, 64, colors.statusSuccess)
                EventStatusBadge("Pembayaran lunas", StatusTone.Success)
                EventTitle("Pembayaran diterima.")
                EventText(
                    "Slot ${summary.category} terkonfirmasi dan race pass otomatis tersimpan.",
                    color = colors.statusSuccess
                )
            }
        }

        RegistrationSummaryCard(summary, "Total pembayaran ${rupiah(summary.total)}", paidBadge = true)

        EventPanel("Detail transaksi") {
            HorizontalDivider(color = colors.border)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EventIcon(R.drawable.wallet, tint = colors.textBrand)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    EventCaption("Referensi transaksi")
                    EventText(reference)
                }
            }
            EventCaption(paidAt)
        }
    }
}

@Composable
fun PaymentFailedScreen(
    onBack: () -> Unit,
    onTryOtherMethod: () -> Unit,
    onCheckStatus: () -> Unit,
    summary: CheckoutSummary = CheckoutSummary.Sample
) {
    val colors = JatrilrunTheme.colors

    EventScreen("Pembayaran gagal", onBack, bottomBar = {
        EventFooter("Coba metode lain", onTryOtherMethod)
    }) {
        EventCard(color = ErrorTint) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EventIcon(R.drawable.close, 64, colors.statusError)
                EventStatusBadge("Pembayaran gagal", StatusTone.Error)
                EventTitle("Pembayaran belum\nberhasil.")
                EventBody("Transaksi tidak dapat diproses. Periksa saldo atau gunakan metode lain.")
            }
        }

        ErrorNotice(
            "Belum ada pembayaran terkonfirmasi",
            "Jika saldo sudah terpotong, cek status sebelum mencoba kembali."
        )

        RegistrationSummaryCard(summary, "Total pembayaran ${rupiah(summary.total)}")

        EventAction("Cek status pembayaran", onCheckStatus, secondary = true)

        EventCaption(
            "Data peserta dan pilihan ${summary.category} tetap tersimpan. " +
                "Kuota diperiksa kembali saat Anda melanjutkan."
        )
    }
}
