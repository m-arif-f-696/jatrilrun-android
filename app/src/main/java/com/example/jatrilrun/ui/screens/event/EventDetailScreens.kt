package com.example.jatrilrun.ui.screens.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrShapes

@Composable
internal fun EventDetail(
    event: TrailEvent,
    onBack: () -> Unit,
    onRoute: () -> Unit,
    onSchedule: () -> Unit,
    onCategories: () -> Unit
) {
    val bali = event.id == baliTrailRun.id
    EventScreen("Detail event", onBack, bottomBar = {
        if (bali) EventFooter("Daftar · mulai ${rupiah(event.minimumPrice)}", onCategories)
    }) {
        EventPhoto(event.image, Modifier.fillMaxWidth().height(180.dp).clip(JrShapes.Large))
        EventPanel {
            EventBadge("Pendaftaran buka")
            EventTitle(event.title)
            if (bali) EventBody("Jelajahi Kintamani, temukan versi dirimu yang lebih berani.")
        }
        EventPanel("Info event") {
            InfoDivider()
            InfoRow(R.drawable.calendar, if (bali) "Tanggal & start" else "Tanggal",
                if (bali) "Minggu, 15 November 2026 · 05.00 WITA" else "${event.day} ${event.month}")
            InfoDivider()
            InfoRow(R.drawable.pin, "Lokasi", if (bali) "Lapangan Kintamani, Bangli, Bali" else event.location)
            if (bali) {
                InfoDivider()
                InfoRow(R.drawable.shield, "Penyelenggara", "Bali Trail Community · Informasi & bantuan")
            }
        }
        if (bali) {
            EventTabs("Info", {}, onRoute, onSchedule)
            EventPanel("Tantangan untuk setiap pelari") {
                EventBody(event.categories.joinToString("\n") { "${it.distance} · ${it.metrics}" })
            }
            EventNotice("48 slot tersedia untuk 30K", "Pilih kategori sesuai kesiapanmu. Harga dan rute terlihat sebelum mendaftar.")
            EventPanel("Fasilitas & race kit") {
                EventBody("Jersey, BIB, medali finisher, e-certificate, makanan setelah finish, water station, dan dukungan medis.")
            }
            EventPanel("Syarat peserta") {
                EventBody("Usia minimum 18 tahun untuk 30K. Bawa air minimal 1 liter, peluit, jaket hujan, ponsel aktif, dan selimut darurat. Detail 50K/100K tersedia pada panduan kategori.")
            }
            EventPanel("Jadwal penting") {
                InfoRow(R.drawable.calendar, "Race kit collection", "14 Nov · 10.00–17.00 WITA · Balai Kintamani")
                InfoRow(R.drawable.clock, "Briefing & race", "14 Nov 17.00 · Start 30K: 15 Nov 05.00")
                EventCaption("Pembayaran tidak dapat dikembalikan. Pemindahan nama sampai 1 November 2026 melalui bantuan peserta.")
            }
            EventLink("Lihat rute & titik bantuan", onRoute, Modifier.fillMaxWidth())
        } else {
            EventPanel("Kategori lomba") {
                EventBody(event.distanceLabel)
                EventText("Mulai ${rupiah(event.minimumPrice)}", color = JatrilrunTheme.colors.textBrand)
            }
        }
    }
}

@Composable
internal fun EventRoute(onBack: () -> Unit, onCategories: () -> Unit) {
    var showGpxNotice by rememberSaveable { mutableStateOf(false) }
    EventScreen("Rute & keselamatan", onBack, bottomBar = {
        EventFooter("Pilih kategori", onCategories)
    }) {
        EventTitle("Kenali medanmu.")
        EventBody("30K · Trail discovery")
        EventPanel {
            Image(
                painterResource(R.drawable.event_elevation),
                contentDescription = "Grafik profil elevasi konseptual 30 kilometer",
                modifier = Modifier.fillMaxWidth().aspectRatio(318f / 141.737f),
                contentScale = ContentScale.Fit
            )
            EventCaption("Profil elevasi konseptual · 0–30 km")
        }
        EventPanel {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("30 km" to "Jarak", "1.450 m" to "Elevasi", "8 jam" to "COT").forEach { (value, label) ->
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        EventHeading(value)
                        EventCaption(label)
                    }
                }
            }
        }
        EventAction("Unduh rute GPX", { showGpxNotice = true }, secondary = true)
        EventPanel("Water station & medis") {
            EventBody("Water station di km 5, 10, 15, 20, dan 25. Tim medis berada di start/finish dan km 15. Marshal berjaga di persimpangan.")
            InfoRow(R.drawable.clock, "Batas waktu", "Finish sebelum 13.00 WITA. Cut-off km 15 pukul 09.00 WITA.")
        }
        EventNotice(
            "Sesuaikan dengan kondisi tubuhmu",
            "Rute menanjak dan permukaan tidak rata. Pelajari perlengkapan wajib sebelum mendaftar.",
            warning = true
        )
    }
    if (showGpxNotice) {
        AlertDialog(
            onDismissRequest = { showGpxNotice = false },
            title = { Text("Unduh rute GPX") },
            text = { Text("File GPX resmi untuk event ini belum tersedia.") },
            confirmButton = { TextButton(onClick = { showGpxNotice = false }) { Text("Tutup") } }
        )
    }
}

@Composable
internal fun EventSchedule(onBack: () -> Unit, onRoute: () -> Unit, onCategories: () -> Unit) {
    EventScreen("Jadwal event", onBack, bottomBar = {
        EventFooter("Pilih kategori", onCategories)
    }) {
        EventTitle("Siap sejak sehari\nsebelumnya.")
        EventTabs("Jadwal", onBack, onRoute, {})
        EventPanel("Agenda event") {
            listOf(
                "Pengambilan race kit" to "Sabtu, 14 Nov · 10.00–17.00 WITA",
                "Briefing peserta" to "Sabtu, 14 Nov · 17.00–18.00 WITA",
                "Area start dibuka" to "Minggu, 15 Nov · 04.00 WITA",
                "Flag off 30K" to "Minggu, 15 Nov · 05.00 WITA",
                "Cut-off finish 30K" to "Minggu, 15 Nov · 13.00 WITA"
            ).forEach { (label, value) ->
                InfoDivider()
                InfoRow(R.drawable.clock, label, value)
            }
        }
        EventNotice("Datang 60 menit sebelum start", "Siapkan race pass, BIB, dan perlengkapan wajib untuk pemeriksaan.")
    }
}

@Composable
internal fun EventCategories(onBack: () -> Unit, onContinue: (String) -> Unit) {
    var selectedDistance by rememberSaveable { mutableStateOf("") }
    val categories = baliTrailRun.categories
    val selected = categories.firstOrNull { it.distance == selectedDistance }
    EventScreen("Pilih kategori", onBack, bottomBar = {
        EventFooter(
            selected?.let { "Lanjut · ${rupiah(it.price)}" } ?: "Pilih kategori untuk lanjut",
            { selected?.let { onContinue(it.distance) } },
            enabled = selected != null
        )
    }) {
        EventPanel {
            EventTitle("Pilih tantanganmu.")
            EventBody("Bandingkan jarak, elevasi, waktu, dan harga.")
        }
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            categories.forEach { category ->
                CategoryCard(category, category == selected) { selectedDistance = category.distance }
            }
        }
        if (selected != null) {
            val start = if (selected.distance == "30K") "05.00" else "03.00"
            EventNotice(
                "${selected.distance} dipilih",
                "Start $start WITA · Usia min. 18 tahun. Biaya layanan Rp5.000 ditampilkan di ringkasan."
            )
        }
    }
}

@Composable
internal fun SoldOutEvent(onBack: () -> Unit, onBrowse: () -> Unit) {
    EventScreen("Kuota habis", onBack, bottomBar = {
        EventFooter("Pendaftaran ditutup", {}, enabled = false)
    }) {
        EventPanel { EventTitle("Sampai jumpa di\ngaris start berikutnya.") }
        EventListingCard(jogjaCityRun, onClick = null, photo = R.drawable.event_trail)
        EventNotice(
            "Semua kategori sudah penuh",
            "Pendaftaran baru tidak tersedia. Jelajahi event lain dengan kuota terbuka.",
            warning = true
        )
        EventAction("Jelajahi event lain", onBrowse, secondary = true)
    }
}

@Composable
private fun CategoryCard(category: RaceCategory, selected: Boolean, onSelect: () -> Unit) {
    val colors = JatrilrunTheme.colors
    val available = category.slots > 0
    EventCard(
        modifier = Modifier.selectable(selected, available, Role.RadioButton, onClick = onSelect),
        color = if (selected) EventInfoTint else EventCardWhite,
        selected = selected
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth().heightIn(min = 46.dp), verticalAlignment = Alignment.CenterVertically) {
                EventText(category.title, JatrilrunTheme.typography.subheading, modifier = Modifier.weight(1f))
                Box(
                    Modifier.padding(horizontal = 16.dp).size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Box(Modifier.fillMaxSize().background(colors.actionPrimary, JrShapes.Pill), contentAlignment = Alignment.Center) {
                            EventIcon(R.drawable.check, size = 16, tint = colors.textOnPrimary)
                        }
                    } else {
                        Box(
                            Modifier.fillMaxSize()
                                .background(colors.backgroundPrimary, JrShapes.Pill)
                                .border(1.dp, colors.textSecondary, JrShapes.Pill)
                        )
                    }
                }
            }
            EventBody(category.metrics)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                EventText(rupiah(category.price), JatrilrunTheme.typography.label, colors.textBrand, Modifier.weight(3f))
                EventCaption(
                    if (available) "Sisa ${category.slots} slot" else "Kuota habis",
                    if (available) colors.statusSuccess else colors.statusError,
                    Modifier.weight(2f)
                )
            }
        }
    }
}

@Composable
private fun EventTabs(active: String, onInfo: () -> Unit, onRoute: () -> Unit, onSchedule: () -> Unit) {
    val colors = JatrilrunTheme.colors
    Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Info" to onInfo, "Rute" to onRoute, "Jadwal" to onSchedule).forEach { (label, onClick) ->
            val selected = label == active
            Box(
                Modifier.weight(1f).heightIn(min = 44.dp)
                    .clip(JrShapes.Medium)
                    .background(if (selected) colors.secondaryAccent else colors.backgroundPrimary)
                    .selectable(selected, role = Role.Tab, onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                EventText(label, JatrilrunTheme.typography.label, if (selected) colors.textBrand else colors.textSecondary)
            }
        }
    }
}

@Composable
private fun InfoRow(icon: Int, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        EventIcon(icon, tint = JatrilrunTheme.colors.textBrand)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            EventCaption(label)
            EventText(value)
        }
    }
}

@Composable
private fun InfoDivider() = HorizontalDivider(color = JatrilrunTheme.colors.border)
