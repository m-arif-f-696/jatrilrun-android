package com.example.jatrilrun.ui.screens.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.components.JrBottomNavigationBar
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrShapes

/** State daftar event. TODO: ganti dengan state dari ViewModel saat API event sudah ada. */
internal enum class EventListState { Loading, Content, Error }

private val SkeletonBar = Color(0xFF918B86)

/** Kartu state: ikon 40dp, judul, isi, tombol opsional, dan catatan opsional di bawahnya. */
@Composable
internal fun EventStateCard(
    icon: Int,
    iconTint: Color,
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    footnote: String? = null
) {
    val typography = JatrilrunTheme.typography
    EventCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                Modifier.fillMaxWidth().background(JatrilrunTheme.colors.backgroundPrimary)
                    .padding(horizontal = 8.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EventIcon(icon, 40, iconTint)
                EventText(
                    title,
                    typography.subheading.copy(textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
                EventText(
                    body,
                    typography.bodySmall.copy(textAlign = TextAlign.Center),
                    JatrilrunTheme.colors.textSecondary,
                    Modifier.fillMaxWidth()
                )
                if (actionLabel != null) RecoveryButton(actionLabel, onAction)
            }
            if (footnote != null) EventBody(footnote)
        }
    }
}

/** Tombol hitam tanpa panah untuk aksi pemulihan ("Coba lagi", "Reset filter"). */
@Composable
private fun RecoveryButton(label: String, onClick: () -> Unit) {
    val colors = JatrilrunTheme.colors
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = JrShapes.Medium,
        colors = ButtonDefaults.buttonColors(containerColor = colors.backgroundInverse, contentColor = colors.textOnPrimary)
    ) {
        EventText(label, JatrilrunTheme.typography.label, colors.textOnPrimary)
    }
}

/** Kolom pencarian non-interaktif untuk layar state. */
@Composable
private fun StaticSearchField(text: String) {
    val tint = JatrilrunTheme.colors.textSecondary
    Row(
        EventFieldBorder(Modifier.fillMaxWidth(), filled = true).heightIn(min = 52.dp).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EventIcon(R.drawable.search, tint = tint)
        EventText(text, JatrilrunTheme.typography.body, tint)
    }
}

@Composable
private fun EventSkeletonCard() {
    Box(
        Modifier.fillMaxWidth().height(110.dp)
            .background(JatrilrunTheme.colors.backgroundSubtle, JrShapes.Medium)
            .padding(16.dp)
    ) {
        Box(Modifier.width(230.dp).height(14.dp).background(SkeletonBar))
    }
}

/** Frame "Memuat event". */
@Composable
internal fun EventLoadingScreen(onBack: () -> Unit, bottomBar: @Composable () -> Unit) {
    EventScreen("Memuat event", onBack, bottomBar = bottomBar) {
        StaticSearchField("Cari event atau kota")
        EventStateCard(
            R.drawable.refresh, JatrilrunTheme.colors.textBrand,
            "Menyiapkan event pilihanmu", "Sebentar, kami sedang mengambil data."
        )
        repeat(3) { EventSkeletonCard() }
    }
}

/** Frame "Koneksi bermasalah". */
@Composable
internal fun EventErrorScreen(onBack: () -> Unit, onRetry: () -> Unit, bottomBar: @Composable () -> Unit) {
    EventScreen("Koneksi bermasalah", onBack, bottomBar = bottomBar) {
        StaticSearchField("Cari event atau kota")
        EventStateCard(
            R.drawable.info, JatrilrunTheme.colors.statusError,
            "Event belum dapat dimuat", "Periksa koneksi Anda, lalu coba kembali.",
            actionLabel = "Coba lagi", onAction = onRetry
        )
    }
}

/** Kartu "Hasil kosong", dipakai di dalam SearchResults. */
@Composable
internal fun EmptyResultsCard(onReset: () -> Unit) {
    EventStateCard(
        R.drawable.search, JatrilrunTheme.colors.textBrand,
        "Belum ada event yang cocok", "Coba ubah lokasi, tanggal, atau jarak lari.",
        actionLabel = "Reset filter", onAction = onReset,
        footnote = "Tidak ada event untuk pilihan ini. Hapus filter jarak atau perluas lokasi."
    )
}

@Composable
private fun PreviewNav() = JrBottomNavigationBar("event", containerColor = EventCardWhite)

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EventLoadingPreview() = JatrilrunTheme { EventLoadingScreen({}) { PreviewNav() } }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EventErrorPreview() = JatrilrunTheme { EventErrorScreen({}, {}) { PreviewNav() } }

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EventEmptyPreview() = JatrilrunTheme {
    SearchResults(
        query = "Trail 100K di Yogyakarta",
        filters = EventFilters(distance = "100K"),
        onBack = {}, onSearch = {}, onFilter = {}, onEvent = {},
        bottomBar = { PreviewNav() }
    )
}
