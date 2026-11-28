package com.example.jatrilrun.ui.screens.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.jatrilrun.R
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jatrilrun.data.remote.api.ApiService
import com.example.jatrilrun.data.remote.dto.EventDto
import com.example.jatrilrun.data.remote.dto.CategoryDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ServerEventState {
    data object Loading : ServerEventState
    data class Ready(val events: List<EventDto>) : ServerEventState
    data class Error(val message: String) : ServerEventState
}

@HiltViewModel
class ServerEventViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    private val _state = MutableStateFlow<ServerEventState>(ServerEventState.Loading)
    val state = _state.asStateFlow()
    val detail = mutableStateOf<EventDto?>(null)
    val detailError = mutableStateOf<String?>(null)

    init { reload() }

    fun reload() = viewModelScope.launch {
        _state.value = ServerEventState.Loading
        _state.value = try {
            val response = api.events()
            if (response.isSuccessful) ServerEventState.Ready(response.body()?.data.orEmpty())
            else ServerEventState.Error("Server mengembalikan status ${response.code()}.")
        } catch (error: Exception) {
            ServerEventState.Error(error.localizedMessage ?: "Tidak dapat terhubung ke server.")
        }
    }

    fun open(slug: String) = viewModelScope.launch {
        detail.value = null
        detailError.value = null
        try {
            val response = api.event(slug)
            if (response.isSuccessful && response.body()?.data != null) {
                detail.value = response.body()?.data
            } else {
                detailError.value = "Detail event gagal dimuat (${response.code()})."
            }
        } catch (error: Exception) {
            detailError.value = error.localizedMessage ?: "Detail event gagal dimuat."
        }
    }
}

internal fun EventDto.toListing(): TrailEvent {
    val trail = type == "trail_run"
    val date = runCatching {
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(startDate.orEmpty().take(10))
    }.getOrNull()
    val locale = Locale.forLanguageTag("id-ID")
    val options = categories.orEmpty()
    return TrailEvent(
        id = id, title = name, day = 0,
        region = startLocation.orEmpty().substringAfterLast(',').trim(),
        location = startLocation.orEmpty(),
        type = when (type) {
            "trail_run" -> "Trail run"
            "run" -> "Road run"
            else -> "Virtual"
        },
        distances = options.mapNotNull { it.type?.name },
        minimumPrice = options.mapNotNull { it.price?.toInt() }.minOrNull() ?: 0,
        image = if (trail) R.drawable.event_trail else R.drawable.event_road,
        serverSlug = slug,
        serverDate = date?.let { SimpleDateFormat("d MMM yyyy", locale).format(it) } ?: "Jadwal belum tersedia",
        serverMonth = date?.let { SimpleDateFormat("MMMM yyyy", locale).format(it) },
        registrationLabel = when {
            status.equals("completed", true) -> "Event selesai"
            options.isNotEmpty() && options.all { it.quota == 0 } -> "Kuota habis"
            status.equals("published", true) && options.any { (it.quota ?: 0) > 0 } -> "Pendaftaran buka"
            else -> "Lihat kategori"
        }
    )
}

@Composable
internal fun ServerEventFeedback(state: ServerEventState, onRetry: () -> Unit) {
    when (state) {
        ServerEventState.Loading -> EventCaption("Memuat event...")
        is ServerEventState.Error -> {
            EventNotice("Event belum dapat dimuat", state.message, warning = true)
            EventAction("Coba lagi", onRetry, secondary = true)
        }
        is ServerEventState.Ready -> if (state.events.isEmpty()) {
            EventBody("Belum ada event yang tersedia.")
            EventAction("Muat ulang", onRetry, secondary = true)
        }
    }
}

@Composable
internal fun ServerEventDetail(
    slug: String,
    onBack: () -> Unit,
    onSelectCategory: (EventDto, CategoryDto) -> Unit,
    viewModel: ServerEventViewModel
) {
    LaunchedEffect(slug) { viewModel.open(slug) }
    val event = viewModel.detail.value?.takeIf { it.slug == slug }
    EventScreen("Detail event", onBack) {
        val error = viewModel.detailError.value
        if (event != null) {
            EventListingCard(event.toListing(), onClick = null)
            event.description?.let { EventBody(it) }
            EventHeading("Kategori lomba")
            event.categories.orEmpty().forEach { category ->
                EventPanel(category.type?.name ?: "Kategori") {
                    EventText(rupiah(category.price?.toInt() ?: 0))
                    if (!event.status.equals("completed", true) && category.quota != 0) {
                        EventAction("Daftar ${category.type?.name ?: "kategori"}", { onSelectCategory(event, category) })
                    }
                }
            }
        } else if (error != null) {
            EventNotice("Gagal memuat detail", error, warning = true)
            EventAction("Coba lagi", { viewModel.open(slug) })
        } else {
            EventCaption("Memuat detail event...")
        }
    }
}
