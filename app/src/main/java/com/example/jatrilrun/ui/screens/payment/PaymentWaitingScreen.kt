package com.example.jatrilrun.ui.screens.payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.ui.screens.checkout.CheckoutSummary
import com.example.jatrilrun.ui.screens.checkout.EventStatusBadge
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrShapes

@Composable
fun PaymentWaitingScreen(
    method: PaymentMethod,
    onBack: () -> Unit,
    onChangeMethod: () -> Unit,
    onCheckStatus: () -> Unit,
    summary: CheckoutSummary = CheckoutSummary.Sample,
    vaNumber: String = DemoVaNumber,
    deadline: String = "Bayar sebelum 7 Okt 2026, 14.30 WIB." // DEMO: ganti dengan batas waktu dari backend
) {
    val context = LocalContext.current
    val typography = JatrilrunTheme.typography
    val colors = JatrilrunTheme.colors

    EventScreen("Menunggu pembayaran", onBack, bottomBar = {
        EventFooter("Cek status pembayaran", onCheckStatus)
    }) {
        EventNotice("Simulasi pembayaran", "Nomor pembayaran dan status pada layar ini adalah contoh. Tidak ada transaksi yang dibuat di server.")
        EventCard(color = EventInfoTint) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EventStatusBadge("Menunggu pembayaran")
                EventTitle("Amankan slotmu\nsebelum waktu habis.")
                EventText(deadline, typography.label, colors.statusWarning)
            }
        }

        EventCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EventText(method.title, typography.label)
                if (method.isVirtualAccount) {
                    EventText(vaNumber, typography.heading)
                    EventAction("Salin nomor VA", { copyVa(context, vaNumber) }, secondary = true)
                } else {
                    // TODO: kode QRIS (frame "QRIS / demo payment code" di Figma disembunyikan)
                    Box(
                        Modifier.size(172.dp).border(1.dp, colors.border, JrShapes.Medium),
                        contentAlignment = Alignment.Center
                    ) { EventCaption("Kode QRIS") }
                }
                EventText(rupiah(summary.total), typography.title, colors.textBrand)
            }
        }

        EventCaption("Penerima: Jatrilrun · BTR ${summary.category}")

        EventPanel("Cara membayar") {
            EventBody(
                "1. Buka aplikasi bank dan pilih Virtual Account.\n" +
                    "2. Masukkan nomor VA di atas.\n" +
                    "3. Periksa nama penerima dan nominal.\n" +
                    "4. Konfirmasi transaksi, lalu cek status di sini."
            )
        }

        EventNotice(
            "Sudah bayar? Tunggu konfirmasi",
            "Pembaruan status dapat memerlukan beberapa menit. Jangan membayar ulang."
        )

        EventLink("Ubah metode pembayaran", onChangeMethod, Modifier.fillMaxWidth().heightIn(min = 52.dp))
    }
}

private fun copyVa(context: Context, vaNumber: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("Nomor VA", vaNumber.filter(Char::isDigit)))
    // Android 13+ menampilkan konfirmasi sendiri.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(context, "Nomor VA disalin", Toast.LENGTH_SHORT).show()
    }
}
