package com.example.jatrilrun.ui.screens.racepass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.screens.checkout.EventStatusBadge
import com.example.jatrilrun.ui.screens.checkout.StatusTone
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.ui.theme.JatrilrunTheme

/** TODO: isi dari backend setelah pembayaran terkonfirmasi (sekarang [Sample] dari Figma). */
data class RacePass(
    val eventTitle: String,
    val category: String,
    val date: String,
    val startTime: String,
    val participantName: String,
    val bibNumber: String,
    val jersey: String,
    val code: String,
    val raceKitSchedule: String,
    val raceKitVenue: String,
    val location: String = ""
) {
    companion object {
        val Sample = RacePass(
            eventTitle = "Bali Trail Run 2026",
            category = "30K",
            date = "15 Nov 2026",
            startTime = "05.00 WITA",
            participantName = "Iqbal Pratama",
            bibNumber = "03048",
            jersey = "M",
            code = "JTR-26-03048",
            raceKitSchedule = "Race kit · 14 Nov, 10.00–17.00 WITA",
            raceKitVenue = "Balai Kintamani · Bawa identitas asli.",
            location = "Kintamani"
        )
    }
}

@Composable
fun RacePassScreen(
    onBack: () -> Unit,
    onBackToHome: () -> Unit,
    pass: RacePass = RacePass.Sample
) {
    val colors = JatrilrunTheme.colors
    val typography = JatrilrunTheme.typography

    EventScreen("Race pass", onBack, bottomBar = {
        EventFooter("Kembali ke Beranda", onBackToHome)
    }) {
        EventCard {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(R.drawable.event_logo),
                        contentDescription = "Jatrilrun",
                        modifier = Modifier.size(width = 94.dp, height = 44.dp),
                        contentScale = ContentScale.Fit
                    )
                    EventText("RACE PASS", typography.label)
                }
                EventStatusBadge("Pendaftaran valid", StatusTone.Success)
                EventText(pass.eventTitle, typography.heading)
                EventBody("${pass.category} · ${pass.date} · ${pass.startTime}")
                EventText(pass.participantName, typography.subheading)
                EventBody("BIB ${pass.bibNumber} · Jersey ${pass.jersey}")
                // DEMO: gambar QR statis berisi kode pass. Ganti dengan QR yang di-generate dari pass.code.
                Image(
                    painterResource(R.drawable.qr_demo_registration),
                    contentDescription = "Kode QR ${pass.code}",
                    modifier = Modifier.size(172.dp).background(colors.backgroundPrimary)
                )
                EventText(pass.code, typography.label)
                EventBody("${pass.raceKitSchedule}\n${pass.raceKitVenue}")
            }
        }
        EventCaption("Race pass otomatis tersimpan setelah pembayaran berhasil.")
    }
}
