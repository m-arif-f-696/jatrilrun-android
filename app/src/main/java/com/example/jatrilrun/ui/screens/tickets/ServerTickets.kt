package com.example.jatrilrun.ui.screens.tickets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jatrilrun.data.remote.api.ApiService
import com.example.jatrilrun.data.remote.dto.RegistrationDto
import com.example.jatrilrun.data.remote.dto.TicketDto
import com.example.jatrilrun.ui.screens.event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ServerTicketsState {
    data object Loading : ServerTicketsState
    data object SignedOut : ServerTicketsState
    data class Ready(val registrations: List<RegistrationDto>) : ServerTicketsState
    data class Error(val message: String) : ServerTicketsState
}

@HiltViewModel
class ServerTicketsViewModel @Inject constructor(private val api: ApiService) : ViewModel() {
    private val _state = MutableStateFlow<ServerTicketsState>(ServerTicketsState.Loading)
    val state = _state.asStateFlow()
    private val _ticket = MutableStateFlow<TicketDto?>(null)
    val ticket = _ticket.asStateFlow()
    private val _ticketError = MutableStateFlow<String?>(null)
    val ticketError = _ticketError.asStateFlow()

    init { reload() }

    fun reload() = viewModelScope.launch {
        _state.value = ServerTicketsState.Loading
        _state.value = try {
            val response = api.registrations()
            when {
                response.code() == 401 -> ServerTicketsState.SignedOut
                response.isSuccessful -> ServerTicketsState.Ready(response.body()?.data.orEmpty())
                else -> ServerTicketsState.Error("Server mengembalikan status ${response.code()}.")
            }
        } catch (error: Exception) {
            ServerTicketsState.Error(error.localizedMessage ?: "Tiket server gagal dimuat.")
        }
    }

    fun openTicket(registrationId: String) = viewModelScope.launch {
        _ticketError.value = null
        try {
            val response = api.ticket(registrationId)
            if (response.isSuccessful && response.body()?.data != null) {
                _ticket.value = response.body()?.data
            } else {
                _ticketError.value = "Tiket belum tersedia (${response.code()})."
            }
        } catch (error: Exception) {
            _ticketError.value = error.localizedMessage ?: "Tiket gagal dimuat."
        }
    }
}

@Composable
internal fun ServerTicketsPanel(viewModel: ServerTicketsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val ticket by viewModel.ticket.collectAsState()
    val ticketError by viewModel.ticketError.collectAsState()
    EventPanel("Pendaftaran di server") {
        when (val current = state) {
            ServerTicketsState.Loading -> EventCaption("Memuat pendaftaran dari server...")
            ServerTicketsState.SignedOut -> EventBody("Masuk dengan akun terverifikasi untuk melihat pendaftaran asli.")
            is ServerTicketsState.Error -> EventNotice("Gagal memuat pendaftaran", current.message, warning = true)
            is ServerTicketsState.Ready -> {
                if (current.registrations.isEmpty()) EventBody("Belum ada pendaftaran pada akun ini.")
                current.registrations.forEach { registration ->
                    EventAction(
                        "${registration.event?.name ?: "Event"} · ${registration.status ?: "PENDING"}",
                        { viewModel.openTicket(registration.id) },
                        secondary = true
                    )
                }
            }
        }
        ticket?.let { actual ->
            EventText(actual.eventName ?: "Race pass")
            EventBody("${actual.runnerName.orEmpty()} · BIB ${actual.bibNumber.orEmpty()} · ${actual.categoryName.orEmpty()}")
            EventCaption("Token tiket server: ${actual.token.orEmpty()}")
        }
        ticketError?.let { EventCaption(it) }
        EventAction("Muat ulang dari server", { viewModel.reload() }, secondary = true)
    }
}
