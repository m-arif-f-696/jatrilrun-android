package com.example.jatrilrun.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jatrilrun.data.local.LocalTicketStore
import com.example.jatrilrun.data.remote.api.ApiService
import com.example.jatrilrun.data.remote.dto.CategoryDto
import com.example.jatrilrun.data.remote.dto.EmergencyContactRequest
import com.example.jatrilrun.data.remote.dto.EventDto
import com.example.jatrilrun.data.remote.dto.ProfileRequest
import com.example.jatrilrun.data.remote.dto.RegistrationRequest
import com.example.jatrilrun.ui.screens.racepass.RacePass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Pemilik draft pendaftaran. Diambil sekali di NavGraph (scope Activity) lalu dibagi ke semua layar alur daftar.
 * Draft ada di memori: selamat dari rotasi, TIDAK selamat jika proses aplikasi dimatikan sistem.
 * TODO: simpan draft ke DataStore/Room atau backend (POST /api/registrations/draft) agar benar-benar "tersimpan".
 */
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val ticketStore: LocalTicketStore,
    private val api: ApiService
) : ViewModel() {

    private val _draft = MutableStateFlow(RegistrationDraft())
    val draft: StateFlow<RegistrationDraft> = _draft.asStateFlow()

    val tickets: StateFlow<List<RacePass>> = ticketStore.tickets
    private val _submitState = MutableStateFlow<LiveRegistrationState>(LiveRegistrationState.Idle)
    val submitState = _submitState.asStateFlow()

    /** Dipanggil dari layar Pilih kategori. Pendaftaran yang sudah lunas tidak membawa data lama. */
    fun selectCategory(distance: String) {
        _draft.update { current ->
            val base = if (current.completed) RegistrationDraft() else current
            base.copy(categoryDistance = distance, serverEventId = "", serverCategoryId = "", consentAccepted = false)
        }
    }

    fun selectServerCategory(event: EventDto, category: CategoryDto) {
        _draft.value = RegistrationDraft(
            serverEventId = event.id,
            serverCategoryId = category.id,
            serverEventName = event.name,
            serverCategoryName = category.type?.name ?: "Kategori",
            serverEventDate = event.startDate.orEmpty(),
            serverEventLocation = event.startLocation.orEmpty(),
            serverCategoryPrice = category.price?.toInt() ?: 0
        )
        _submitState.value = LiveRegistrationState.Idle
    }

    fun saveParticipant(form: ParticipantForm) = _draft.update { it.copy(participant = form) }

    fun saveEmergency(form: EmergencyForm) = _draft.update { it.copy(emergency = form) }

    fun acceptConsent() = _draft.update { it.copy(consentAccepted = true) }

    fun submitLiveRegistration(onCreated: (String) -> Unit) = viewModelScope.launch {
        val snapshot = _draft.value
        if (snapshot.serverEventId.isBlank() || snapshot.serverCategoryId.isBlank()) return@launch
        _submitState.value = LiveRegistrationState.Loading
        try {
            val runner = snapshot.participant
            val emergency = snapshot.emergency
            val session = api.session()
            if (!session.isSuccessful) error("Sesi masuk berakhir. Masuk kembali sebelum mendaftar.")
            if (!session.body()?.data?.email.equals(runner.email.trim(), ignoreCase = true)) {
                error("Email peserta harus sama dengan email akun yang sedang masuk.")
            }
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply { isLenient = false }
                .parse(runner.birthDate)?.let {
                    SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(it)
                } ?: error("Tanggal lahir tidak valid.")
            val profile = ProfileRequest(
                runner.fullName.trim(), runner.phone.trim(),
                if (runner.gender == "Laki-laki") "male" else "female",
                date, runner.idCard.trim(), runner.idCardType.lowercase(Locale.ROOT), runner.bloodType,
                listOf(EmergencyContactRequest(emergency.name.trim(), emergency.relation, emergency.phone.trim()))
            )
            val currentProfile = api.profile()
            val savedProfile = when (currentProfile.code()) {
                200 -> api.updateProfile(profile)
                404 -> api.createProfile(profile)
                else -> error("Profil tidak dapat diperiksa (${currentProfile.code()}).")
            }
            if (!savedProfile.isSuccessful) error("Profil ditolak server (${savedProfile.code()}). ${savedProfile.errorBody()?.string().orEmpty().take(240)}")
            val created = api.createRegistration(RegistrationRequest(snapshot.serverEventId, snapshot.serverCategoryId))
            val id = if (created.isSuccessful) {
                created.body()?.data?.id ?: error("ID pendaftaran tidak ditemukan pada respons server.")
            } else {
                // Percobaan ulang setelah timeout bisa berarti registrasi sudah terbuat.
                val existing = api.registrations()
                existing.body()?.data?.firstOrNull {
                    it.eventId == snapshot.serverEventId && it.categoryId == snapshot.serverCategoryId
                }?.id ?: error("Pendaftaran ditolak server (${created.code()}). ${created.errorBody()?.string().orEmpty().take(240)}")
            }
            _submitState.value = LiveRegistrationState.Created(id)
            onCreated(id)
        } catch (error: Exception) {
            _submitState.value = LiveRegistrationState.Error(error.localizedMessage ?: "Pendaftaran gagal.")
        }
    }

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

sealed interface LiveRegistrationState {
    data object Idle : LiveRegistrationState
    data object Loading : LiveRegistrationState
    data class Created(val id: String) : LiveRegistrationState
    data class Error(val message: String) : LiveRegistrationState
}
