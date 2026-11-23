package com.example.jatrilrun.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrShapes

private val WarningBadgeBg = Color(0xFFECD99F)
private val ErrorBadgeText = Color(0xFFF8D5D1)
internal val SuccessTint = Color(0xFFDCEBDF)
internal val ErrorTint = Color(0xFFFEE2DF)

/** Card "Tahap pendaftaran": 4 segmen + label (mis. "2 dari 4 · Ringkasan"). */
@Composable
internal fun RegistrationStepper(step: Int, label: String, totalSteps: Int = 4) {
    val colors = JatrilrunTheme.colors
    EventCard {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(totalSteps) { index ->
                    Box(
                        Modifier.weight(1f).height(4.dp).background(
                            if (index < step) colors.actionPrimary else colors.backgroundSubtle,
                            JrShapes.Pill
                        )
                    )
                }
            }
            EventCaption(label)
        }
    }
}

/** Checkbox persetujuan: kotak 24dp, area tap min 52dp. */
@Composable
internal fun ConsentCheckbox(checked: Boolean, label: String, onCheckedChange: (Boolean) -> Unit) {
    val colors = JatrilrunTheme.colors
    Row(
        Modifier.fillMaxWidth().heightIn(min = 52.dp)
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(24.dp)
                .background(if (checked) colors.actionPrimary else colors.backgroundPrimary, JrShapes.Small)
                .border(1.dp, if (checked) colors.actionPrimary else colors.textSecondary, JrShapes.Small),
            contentAlignment = Alignment.Center
        ) {
            if (checked) EventIcon(R.drawable.check, 16, colors.textOnPrimary)
        }
        EventText(label, modifier = Modifier.weight(1f))
    }
}

/** Satu pilihan metode pembayaran (radio). */
@Composable
internal fun PaymentMethodItem(title: String, note: String, selected: Boolean, onClick: () -> Unit) {
    val colors = JatrilrunTheme.colors
    EventCard(
        modifier = Modifier.selectable(selected, role = Role.RadioButton, onClick = onClick),
        color = if (selected) EventInfoTint else EventCardWhite,
        selected = selected
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            EventIcon(R.drawable.wallet, 24, colors.textBrand)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                EventText(title, JatrilrunTheme.typography.label)
                EventCaption(note)
            }
            Box(
                Modifier.size(24.dp)
                    .background(if (selected) colors.actionPrimary else colors.backgroundPrimary, JrShapes.Pill)
                    .border(
                        if (selected) 2.dp else 1.dp,
                        if (selected) colors.actionPrimary else colors.textSecondary,
                        JrShapes.Pill
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) EventIcon(R.drawable.check, 16, colors.textOnPrimary)
            }
        }
    }
}

/** Rincian biaya: pendaftaran + biaya layanan = total. */
@Composable
internal fun CostBreakdown(summary: CheckoutSummary) {
    EventPanel {
        CostRow("Pendaftaran ${summary.category}", rupiah(summary.registrationFee))
        CostRow("Biaya layanan", rupiah(summary.serviceFee))
        HorizontalDivider(color = JatrilrunTheme.colors.border)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EventText("Total pembayaran", JatrilrunTheme.typography.label)
            EventText(rupiah(summary.total), JatrilrunTheme.typography.subheading, JatrilrunTheme.colors.textBrand)
        }
    }
}

@Composable
private fun CostRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EventText(label)
        EventText(value, JatrilrunTheme.typography.label)
    }
}

internal enum class StatusTone { Warning, Success, Error }

/** Badge status (kuning = menunggu, hijau = berhasil/valid, merah = gagal). */
@Composable
internal fun EventStatusBadge(text: String, tone: StatusTone = StatusTone.Warning) {
    val colors = JatrilrunTheme.colors
    val (background, content) = when (tone) {
        StatusTone.Warning -> WarningBadgeBg to colors.statusWarning
        StatusTone.Success -> colors.statusSuccess to colors.textOnPrimary
        StatusTone.Error -> colors.statusError to ErrorBadgeText
    }
    Box(
        Modifier.background(background, JrShapes.Pill)
            .heightIn(min = 30.dp).padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        EventCaption(text, content)
    }
}

/** Notice merah (versi error dari EventNotice). */
@Composable
internal fun ErrorNotice(title: String, body: String) {
    val color = JatrilrunTheme.colors.statusError
    Row(
        Modifier.fillMaxWidth().background(ErrorTint, JrShapes.Medium).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EventIcon(R.drawable.info, tint = color)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            EventText(title, JatrilrunTheme.typography.label, color)
            EventBody(body)
        }
    }
}

/** Kartu ringkasan pendaftaran. [highlight] = baris oranye paling bawah. */
@Composable
internal fun RegistrationSummaryCard(summary: CheckoutSummary, highlight: String, paidBadge: Boolean = false) {
    val typography = JatrilrunTheme.typography
    EventPanel {
        EventText(summary.eventTitle, typography.subheading)
        EventBody("${summary.category} · ${summary.date} · ${summary.location}")
        EventText("${summary.participantName} · BIB ${summary.bibName}", typography.label)
        EventCaption(
            listOfNotNull(
                "Jersey ${summary.jersey}",
                summary.community?.takeIf { it.isNotBlank() }?.let { "Komunitas $it" }
            ).joinToString(" · ")
        )
        EventText(highlight, typography.subheading, JatrilrunTheme.colors.textBrand)
        if (paidBadge) EventStatusBadge("Pembayaran lunas", StatusTone.Success)
    }
}
