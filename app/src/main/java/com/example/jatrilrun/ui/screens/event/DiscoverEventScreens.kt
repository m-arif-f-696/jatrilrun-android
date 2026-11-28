package com.example.jatrilrun.ui.screens.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.R
import com.example.jatrilrun.ui.theme.JatrilrunTheme

@Composable
internal fun DiscoverHome(
    onSearch: () -> Unit,
    onBrowse: () -> Unit,
    onTickets: () -> Unit,
    onEvent: (TrailEvent) -> Unit,
    catalog: List<TrailEvent>,
    feedback: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    account: @Composable () -> Unit
) {
    EventScreen(
        topBar = {
            Row(
                Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(R.drawable.event_logo), "Jatrilrun", Modifier.size(94.dp, 44.dp))
                IconButton(onClick = onTickets) {
                    EventIcon(R.drawable.ticket, size = 24, description = "Tiket saya")
                }
            }
        },
        bottomBar = bottomBar
    ) {
        EventTitle("Langkah baru,\ncerita baru.")
        SearchLauncher(onClick = onSearch)
        feedback()
        catalog.firstOrNull()?.let { featured ->
            EventHeading("Pilihan komunitas")
            EventListingCard(featured, { onEvent(featured) }, home = true)
            val nearby = catalog.drop(1).filter { it.location.contains("Yogyakarta", ignoreCase = true) }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EventHeading(if (nearby.isEmpty()) "Event lainnya" else "Dekat Yogyakarta")
                EventLink("Lihat semua", onBrowse)
            }
            (nearby.firstOrNull() ?: catalog.getOrNull(1))?.let { event ->
                EventListingCard(event, { onEvent(event) }, compact = true, home = true)
            }
        }
        account()
    }
}

@Composable
internal fun BrowseEvents(
    catalog: List<TrailEvent> = trailEvents,
    feedback: @Composable () -> Unit = {},
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onFilter: () -> Unit,
    onEvent: (TrailEvent) -> Unit,
    bottomBar: @Composable () -> Unit
) {
    var type by rememberSaveable { mutableStateOf("Semua") }
    val events = EventFilters(type = type.takeUnless { it == "Semua" }.orEmpty()).results("", catalog)
    EventScreen("Jelajahi event", onBack, bottomBar = bottomBar) {
        EventTitle("Temukan garis\nstart berikutnya.")
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SearchLauncher(onClick = onSearch, modifier = Modifier.weight(1f))
            IconButton(onClick = onFilter, modifier = Modifier.size(44.dp)) {
                EventIcon(R.drawable.filter, size = 24, description = "Filter event")
            }
        }
        EventChips(listOf("Semua", "Trail run", "Road run"), type) { type = it }
        feedback()
        if (catalog.isNotEmpty()) EventCaption("${events.size} event")
        events.forEachIndexed { index, event ->
            EventListingCard(
                event, { onEvent(event) }, compact = index != 0,
                date = event.shortDate,
                location = event.region
            )
        }
    }
}

@Composable
internal fun SearchEvents(
    query: String,
    recent: List<String>,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onDistance: (String) -> Unit
) {
    var input by rememberSaveable { mutableStateOf(query) }
    val focusManager = LocalFocusManager.current
    EventScreen("Cari event", onBack) {
        val tint = JatrilrunTheme.colors.textSecondary
        val submit = {
            focusManager.clearFocus()
            onSearch(input.trim())
        }
        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = EventFieldBorder(Modifier.fillMaxWidth().semantics { contentDescription = "Cari event atau kota" }, filled = true),
            textStyle = JatrilrunTheme.typography.body.copy(color = JatrilrunTheme.colors.textPrimary),
            cursorBrush = SolidColor(JatrilrunTheme.colors.actionPrimary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { submit() })
        ) { field ->
            Row(
                Modifier.heightIn(min = 52.dp).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EventIcon(R.drawable.search, tint = tint)
                Box(Modifier.weight(1f)) {
                    if (input.isEmpty()) EventText("Trail run di Bali", JatrilrunTheme.typography.body, tint)
                    field()
                }
                if (input.isNotEmpty()) {
                    IconButton(onClick = submit, modifier = Modifier.size(32.dp)) {
                        EventIcon(R.drawable.arrow, tint = tint, description = "Tampilkan hasil pencarian")
                    }
                }
            }
        }
        EventPanel("Pencarian terakhir") {
            recent.forEach { term ->
                Row(
                    Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .clickable(role = Role.Button) { onSearch(term) }.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EventIcon(R.drawable.clock, tint = tint)
                    EventText(term, JatrilrunTheme.typography.body)
                }
            }
        }
        EventPanel("Jelajahi berdasarkan jarak") {
            EventChips(listOf("10K", "30K", "50K"), "", onSelect = onDistance)
            EventBody("Cari nama event atau kota. Filter membantu memilih tanggal dan anggaran.")
        }
    }
}

@Composable
internal fun SearchResults(
    query: String,
    filters: EventFilters,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onFilter: () -> Unit,
    onEvent: (TrailEvent) -> Unit,
    bottomBar: @Composable () -> Unit,
    onReset: () -> Unit = {},
    catalog: List<TrailEvent> = trailEvents
) {
    val results = filters.results(query, catalog)
    val chips = listOf(filters.region, filters.distance, if (filters.month.isEmpty()) "" else "Nov 2026")
        .filter { it.isNotEmpty() }
        .ifEmpty { listOfNotNull(filters.type.takeIf { it.isNotEmpty() }) }
    EventScreen(if (results.isEmpty()) "Hasil kosong" else "Hasil pencarian", onBack, bottomBar = bottomBar) {
        SearchLauncher(query, onSearch)
        if (chips.isNotEmpty()) {
            if (results.isEmpty()) {
                // Figma "Hasil kosong": chip filter aktif selalu terpilih, lebar mengikuti isi.
                EventChips(chips, selected = chips.first(), fill = false) { onFilter() }
            } else {
                EventChips(chips, filters.region) { onFilter() }
            }
        }
        if (results.isEmpty()) {
            EmptyResultsCard(onReset)
        } else {
            EventCaption("${results.size} event sesuai pilihanmu")
            results.forEachIndexed { index, event -> EventListingCard(event, { onEvent(event) }, compact = index != 0) }
            EventLink("Ubah filter", onFilter, Modifier.fillMaxWidth())
        }
    }
}

internal val EventFiltersSaver = listSaver<EventFilters, String>(
    save = {
        listOf(it.region, it.type, it.distance, it.month, it.price?.name.orEmpty(), it.availableOnly.toString())
    },
    restore = { values ->
        EventFilters(
            region = values[0], type = values[1], distance = values[2], month = values[3],
            price = EventPrice.entries.firstOrNull { it.name == values[4] },
            availableOnly = values[5].toBoolean()
        )
    }
)

@Composable
internal fun FilterEvents(
    catalog: List<TrailEvent> = trailEvents,
    query: String,
    filters: EventFilters,
    onBack: () -> Unit,
    onApply: (EventFilters) -> Unit
) {
    var draft by rememberSaveable(stateSaver = EventFiltersSaver) { mutableStateOf(filters) }
    val count = draft.results(query, catalog).size
    EventScreen("Filter event", onBack, bottomBar = {
        EventFooter("Tampilkan $count event", { onApply(draft) })
    }) {
        EventTitle("Lari sesuai rencanamu.")
        EventDropdown("Lokasi", draft.region, listOf("Bali", "Yogyakarta")) { draft = draft.copy(region = it) }
        EventPanel("Jenis event") {
            EventChips(listOf("Semua", "Trail run", "Road run"), draft.type.ifEmpty { "Semua" }) {
                draft = draft.copy(type = it.takeUnless { type -> type == "Semua" }.orEmpty())
            }
        }
        EventPanel("Jarak lomba") {
            EventChips(listOf("10K", "30K", "50K"), draft.distance) {
                draft = draft.copy(distance = it.takeUnless { distance -> distance == draft.distance }.orEmpty())
            }
            EventDropdown("Bulan event", draft.month, listOf(baliTrailRun.month)) { draft = draft.copy(month = it) }
        }
        EventPanel("Harga pendaftaran") {
            EventChips(EventPrice.entries.map { it.label }, draft.price?.label.orEmpty()) { label ->
                val price = EventPrice.entries.first { it.label == label }
                draft = draft.copy(price = price.takeUnless { it == draft.price })
            }
            Row(
                Modifier.fillMaxWidth().heightIn(min = 52.dp)
                    .toggleable(value = draft.availableOnly, role = Role.Checkbox) {
                        draft = draft.copy(availableOnly = it)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(checked = draft.availableOnly, onCheckedChange = null, modifier = Modifier.padding(8.dp))
                EventText("Hanya event dengan kuota tersedia")
            }
        }
        EventLink("Reset filter", { draft = EventFilters() }, Modifier.fillMaxWidth())
    }
}

@Composable
private fun EventDropdown(
    title: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        EventText(title, JatrilrunTheme.typography.label)
        Box(Modifier.fillMaxWidth()) {
            Row(
                EventFieldBorder(Modifier.fillMaxWidth()).heightIn(min = 52.dp)
                    .clickable(role = Role.DropdownList) { expanded = true }.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EventText(value.ifEmpty { "Semua" }, JatrilrunTheme.typography.body, JatrilrunTheme.colors.textSecondary)
                EventIcon(R.drawable.down, tint = JatrilrunTheme.colors.textSecondary)
            }
            DropdownMenu(expanded, { expanded = false }) {
                (listOf("") + options).forEach { option ->
                    DropdownMenuItem(text = { Text(option.ifEmpty { "Semua" }) }, onClick = {
                        onSelect(option)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
private fun SearchLauncher(query: String = "", onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        EventFieldBorder(modifier.fillMaxWidth(), filled = true).heightIn(min = 52.dp)
            .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tint = JatrilrunTheme.colors.textSecondary
        EventIcon(R.drawable.search, tint = tint)
        EventText(query.ifEmpty { "Cari event atau kota" }, JatrilrunTheme.typography.body, tint)
    }
}
