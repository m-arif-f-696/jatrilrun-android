package com.example.jatrilrun.ui.screens.checkout

import androidx.lifecycle.ViewModel
import com.example.jatrilrun.data.local.LocalTicketStore
import com.example.jatrilrun.ui.screens.racepass.RacePass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Pemilik draft pendaftaran. Diambil sekali di NavGraph (scope Activity) lalu dibagi ke semua layar alur daftar.
 * Draft ada di memori: selamat dari rotasi, TIDAK selamat jika proses aplikasi dimatikan sistem.
 * TODO: simpan draft ke DataStore/Room atau backend (POST /api/registrations/draft) agar benar-benar "tersimpan".
 */
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val ticketStore: LocalTicketStore
) : ViewModel() {

    private val _draft = MutableStateFlow(RegistrationDraft())
    val draft: StateFlow<RegistrationDraft> = _draft.asStateFlow()

    val tickets: StateFlow<List<RacePass>> = ticketStore.tickets

    /** Dipanggil dari layar Pilih kategori. Pendaftaran yang sudah lunas tidak membawa data lama. */
    fun selectCategory(distance: String) {
        _draft.update { current ->
            val base = if (current.completed) RegistrationDraft() else current
            base.copy(categoryDistance = distance, consentAccepted = false)
        }
    }

    fun saveParticipant(form: ParticipantForm) = _draft.update { it.copy(participant = form) }

    fun saveEmergency(form: EmergencyForm) = _draft.update { it.copy(emergency = form) }

    fun acceptConsent() = _draft.update { it.copy(consentAccepted = true) }

    /**
     * DEMO: dipanggil saat "Cek status pembayaran" (yang sementara selalu berhasil) supaya Tiket Saya terisi.
     * TODO: hapus; race pass datang dari backend setelah status pembayaran = paid.
     */
    fun markPaidDemo() {
        val summary = _draft.value.toCheckoutSummary()
        ticketStore.add(
            RacePass(
                eventTitle = summary.eventTitle,
                category = summary.category,
                date = summary.date,
                startTime = if (summary.category == "30K") "05.00 WITA" else "03.00 WITA",
                participantName = summary.participantName,
                bibNumber = "03048",
                jersey = summary.jersey,
                code = "JTR-26-03048",
                raceKitSchedule = RacePass.Sample.raceKitSchedule,
                raceKitVenue = RacePass.Sample.raceKitVenue,
                location = summary.location
            )
        )
        _draft.update { it.copy(completed = true) }
    }
}
