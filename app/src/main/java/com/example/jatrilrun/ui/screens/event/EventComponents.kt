package com.example.jatrilrun.ui.screens.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrShapes

internal val EventCardWhite = Color(0xFFFDFDFC)
private val FieldBorder = Color(0xFF958E89)
private val ClosedTint = Color(0xFFF7D2CE)
private val WarningTint = Color(0xFFF0E7CA)
internal val EventInfoTint = Color(0xFFF7EDE5)

@Composable
internal fun EventText(
    text: String,
    style: TextStyle = JatrilrunTheme.typography.bodySmall,
    color: Color = JatrilrunTheme.colors.textPrimary,
    modifier: Modifier = Modifier
) {
    Text(text, modifier, color = color, style = style)
}

@Composable
internal fun EventTitle(text: String) = EventText(text, JatrilrunTheme.typography.title)

@Composable
internal fun EventHeading(text: String) = EventText(text, JatrilrunTheme.typography.subheading)

@Composable
internal fun EventBody(text: String) = EventText(text, color = JatrilrunTheme.colors.textSecondary)

@Composable
internal fun EventCaption(
    text: String,
    color: Color = JatrilrunTheme.colors.textSecondary,
    modifier: Modifier = Modifier
) = EventText(text, JatrilrunTheme.typography.caption, color, modifier)

@Composable
internal fun EventIcon(
    resource: Int,
    size: Int = 20,
    tint: Color = JatrilrunTheme.colors.textPrimary,
    description: String? = null
) {
    Icon(painterResource(resource), description, Modifier.size(size.dp), tint)
}

@Composable
internal fun EventScreen(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    topBar: (@Composable () -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        containerColor = JatrilrunTheme.colors.backgroundPrimary,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (title != null || topBar != null) {
                Column(
                    Modifier.fillMaxWidth()
                        .background(if (title == null) JatrilrunTheme.colors.backgroundPrimary else EventCardWhite)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                ) {
                    if (title != null && onBack != null) {
                        Row(
                            Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(44.dp)
                                    .background(JatrilrunTheme.colors.backgroundPrimary, JrShapes.Pill)
                            ) { EventIcon(R.drawable.back, description = "Kembali") }
                            EventHeading(title)
                        }
                    }
                    topBar?.invoke()
                }
            }
        },
        bottomBar = {
            if (bottomBar != null) {
                Column(
                    Modifier.fillMaxWidth().background(EventCardWhite)
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                ) { bottomBar() }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
internal fun EventCard(
    modifier: Modifier = Modifier,
    color: Color = EventCardWhite,
    selected: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = JrShapes.Large,
        color = color,
        shadowElevation = if (selected) 0.dp else 3.dp,
        border = if (selected) BorderStroke(2.dp, JatrilrunTheme.colors.actionPrimary)
            else BorderStroke(1.dp, JatrilrunTheme.colors.border),
        content = content
    )
}

@Composable
internal fun EventPanel(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    EventCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (title != null) EventHeading(title)
            content()
        }
    }
}

@Composable
internal fun EventBadge(text: String, closed: Boolean = false, wide: Boolean = false) {
    Box(
        Modifier.then(if (wide) Modifier.width(146.dp) else Modifier)
            .background(if (closed) ClosedTint else JatrilrunTheme.colors.secondaryAccent, JrShapes.Pill)
            .heightIn(min = 30.dp).padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        EventCaption(text, if (closed) JatrilrunTheme.colors.statusError else JatrilrunTheme.colors.textBrand)
    }
}

@Composable
internal fun EventPhoto(resource: Int, modifier: Modifier) {
    Image(painterResource(resource), null, modifier, contentScale = ContentScale.Crop)
}

@Composable
internal fun EventListingCard(
    event: TrailEvent,
    onClick: (() -> Unit)?,
    compact: Boolean = false,
    home: Boolean = false,
    date: String = event.shortDate,
    location: String = event.location,
    photo: Int = event.image
) {
    EventCard(
        modifier = if (onClick == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick),
        color = if (home) JatrilrunTheme.colors.backgroundPrimary else EventCardWhite
    ) {
        if (compact) {
            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EventPhoto(photo, Modifier.width(88.dp).height(104.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    EventText(event.title, JatrilrunTheme.typography.label)
                    EventCaption("$date · $location")
                    EventCaption(event.distanceLabel)
                    EventText("Mulai ${rupiah(event.minimumPrice)}", color = JatrilrunTheme.colors.textBrand)
                    if (event.soldOut) EventBadge("Kuota habis", closed = true)
                }
            }
        } else {
            Column {
                EventPhoto(photo, Modifier.fillMaxWidth().height(168.dp))
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!home) {
                        EventBadge(
                            if (event.soldOut) "Kuota habis" else "Pendaftaran buka",
                            closed = event.soldOut,
                            wide = true
                        )
                    }
                    EventHeading(event.title)
                    EventCaption("$date · $location")
                    if (!home) EventCaption(event.distanceLabel)
                    EventText("Mulai ${rupiah(event.minimumPrice)}", JatrilrunTheme.typography.label, JatrilrunTheme.colors.textBrand)
                }
            }
        }
    }
}

@Composable
internal fun EventAction(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    secondary: Boolean = false
) {
    val colors = JatrilrunTheme.colors
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = JrShapes.Medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (secondary) colors.backgroundInverse else colors.actionPrimary,
            contentColor = colors.textOnPrimary,
            disabledContainerColor = colors.backgroundSubtle,
            disabledContentColor = colors.textSecondary
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            EventText(label, JatrilrunTheme.typography.label, LocalContentColor.current, Modifier.weight(1f, fill = false))
            EventIcon(R.drawable.arrow, tint = LocalContentColor.current)
        }
    }
}

@Composable
internal fun EventFooter(label: String, onClick: () -> Unit, enabled: Boolean = true) {
    HorizontalDivider(color = JatrilrunTheme.colors.border)
    Box(Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 8.dp)) {
        EventAction(label, onClick, enabled)
    }
}

@Composable
internal fun EventLink(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick, modifier.heightIn(min = 48.dp)) {
        EventText(text, JatrilrunTheme.typography.label, JatrilrunTheme.colors.textBrand)
    }
}

@Composable
internal fun EventChips(
    labels: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(Modifier.fillMaxWidth().selectableGroup(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        labels.forEach { label ->
            val active = label == selected
            val colors = JatrilrunTheme.colors
            Surface(
                modifier = Modifier.weight(1f).heightIn(min = 44.dp)
                    .selectable(active, role = Role.RadioButton) { onSelect(label) },
                shape = JrShapes.Pill,
                color = if (active) colors.secondaryAccent else colors.backgroundPrimary,
                border = BorderStroke(1.dp, if (active) colors.actionPrimary else FieldBorder)
            ) {
                Box(Modifier.padding(horizontal = 6.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                    EventText(label, JatrilrunTheme.typography.label, if (active) colors.textBrand else colors.textSecondary)
                }
            }
        }
    }
}

@Composable
internal fun EventNotice(title: String, body: String, warning: Boolean = false) {
    val color = if (warning) JatrilrunTheme.colors.statusWarning else JatrilrunTheme.colors.textBrand
    Row(
        Modifier.fillMaxWidth().background(if (warning) WarningTint else EventInfoTint, JrShapes.Medium).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EventIcon(R.drawable.info, tint = color)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            EventText(title, JatrilrunTheme.typography.label, color)
            EventBody(body)
        }
    }
}

@Composable
internal fun EventFieldBorder(modifier: Modifier = Modifier, filled: Boolean = false): Modifier {
    return modifier
        .background(
            if (filled) JatrilrunTheme.colors.backgroundSubtle else JatrilrunTheme.colors.backgroundPrimary,
            JrShapes.Medium
        )
        .border(1.dp, FieldBorder, JrShapes.Medium)
}
