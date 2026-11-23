package com.example.jatrilrun.data.local

import com.example.jatrilrun.ui.screens.racepass.RacePass
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * DEMO: penyimpanan tiket di memori supaya layar "Tiket Saya" bisa diuji end-to-end sebelum backend siap.
 * TODO: ganti dengan TicketRepository (Room sebagai cache + GET /api/registrations).
 * Sengaja memakai RacePass dari layer UI supaya sederhana; saat repository dibuat, pindahkan ke domain/model.
 */
@Singleton
class LocalTicketStore @Inject constructor() {
    private val _tickets = MutableStateFlow<List<RacePass>>(emptyList())
    val tickets: StateFlow<List<RacePass>> = _tickets.asStateFlow()

    fun add(pass: RacePass) {
        _tickets.update { current -> if (current.any { it.code == pass.code }) current else current + pass }
    }

    fun clear() {
        _tickets.value = emptyList()
    }
}
