package com.example.jatrilrun.ui.screens.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.jatrilrun.R
import com.example.jatrilrun.data.local.LocalTicketStore
import com.example.jatrilrun.ui.components.JrBottomNavigationBar
import com.example.jatrilrun.ui.screens.checkout.EventStatusBadge
import com.example.jatrilrun.ui.screens.checkout.StatusTone
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.ui.screens.racepass.RacePass
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(store: LocalTicketStore) : ViewModel() {
    // TODO: ganti sumbernya dengan TicketRepository (GET /api/registrations) + state Loading/Error.
    val tickets: StateFlow<List<RacePass>> = store.tickets
}

/** Figma "08 / Tiket Saya": daftar race pass (terdaftar) atau empty state (kosong). */
@Composable
fun TicketsScreen(
    tickets: List<RacePass>,
    onBrowse: () -> Unit,
    onOpenPass: (RacePass) -> Unit,
    serverContent: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit
) {
    val colors = JatrilrunTheme.colors
    val typography = JatrilrunTheme.typography

    EventScreen(
        topBar = {
            Row(
                Modifier.fillMaxWidth().background(EventCardWhite).height(64.dp).padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) { EventHeading("Tiket Saya") }
        },
        bottomBar = bottomBar
    ) {
        serverContent()
        if (tickets.isEmpty()) {
            Spacer(Modifier.height(96.dp))
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EventIcon(R.drawable.ticket, 40, colors.textPrimary)
                EventText(
                    "Belum ada event yang diikuti",
                    typography.subheading.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
                EventText(
                    "Ikuti event pertamamu. Race pass akan muncul di sini setelah pembayaran berhasil.",
                    typography.bodySmall.copy(textAlign = TextAlign.Center),
                    colors.textSecondary,
                    Modifier.fillMaxWidth()
                )
                EventAction("Jelajahi event", onBrowse)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                EventText("Event yang Anda ikuti", typography.heading)
                EventBody("${tickets.size} race pass tersimpan")
            }
            tickets.forEach { pass -> TicketCard(pass) { onOpenPass(pass) } }
            EventBody("Race pass otomatis tersimpan setelah pembayaran berhasil. Buka tiket untuk menunjukkan QR saat mengambil race kit.")
        }
    }
}

@Composable
private fun TicketCard(pass: RacePass, onClick: () -> Unit) {
    val typography = JatrilrunTheme.typography
    EventCard(Modifier.clickable(role = Role.Button, onClick = onClick)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            EventText(pass.eventTitle, typography.subheading)
            EventBody(listOfNotNull(pass.category, pass.date, pass.location.takeIf { it.isNotBlank() }).joinToString(" · "))
            EventText("${pass.participantName} · BIB ${pass.bibNumber}", typography.label)
            EventCaption("${pass.code} · Jersey ${pass.jersey}")
            EventText("Race pass tersedia", typography.subheading, JatrilrunTheme.colors.textBrand)
            EventStatusBadge("Pembayaran lunas", StatusTone.Success)
        }
    }
}

@Composable
private fun PreviewNav() = JrBottomNavigationBar("tickets", containerColor = EventCardWhite)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TicketsRegisteredPreview() = JatrilrunTheme {
    TicketsScreen(listOf(RacePass.Sample), {}, {}) { PreviewNav() }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun TicketsEmptyPreview() = JatrilrunTheme {
    TicketsScreen(emptyList(), {}, {}) { PreviewNav() }
}
