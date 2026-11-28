package com.example.jatrilrun.ui.screens.checkout

import com.example.jatrilrun.ui.screens.event.baliTrailRun

/** Isian layar "Data peserta". Dikirim ke backend sebagai bagian dari body pendaftaran. */
data class ParticipantForm(
    val fullName: String = "",
    val bibName: String = "",
    val email: String = "",
    val phone: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val jersey: String = "",
    val community: String = "",
    val idCard: String = "",
    val idCardType: String = "",
    val bloodType: String = ""
)

/** Isian layar "Kontak darurat". */
data class EmergencyForm(
    val name: String = "",
    val relation: String = "",
    val phone: String = "",
    val medical: String = ""
)

/**
 * Satu draft pendaftaran yang dibagi semua layar alur daftar (kategori -> peserta -> kontak darurat
 * -> persetujuan -> ringkasan -> pembayaran). Inilah yang membuat pilihan kategori ikut sampai
 * ringkasan, dan membuat "Data tetap tersimpan" di bottom sheet keluar benar.
 */
data class RegistrationDraft(
    val categoryDistance: String = "",
    val serverEventId: String = "",
    val serverCategoryId: String = "",
    val serverEventName: String = "",
    val serverCategoryName: String = "",
    val serverEventDate: String = "",
    val serverEventLocation: String = "",
    val serverCategoryPrice: Int = 0,
    val participant: ParticipantForm = ParticipantForm(),
    val emergency: EmergencyForm = EmergencyForm(),
    val consentAccepted: Boolean = false,
    /** true setelah pembayaran lunas; pendaftaran berikutnya mulai dari draft kosong. */
    val completed: Boolean = false
)

/** DEMO: biaya layanan dari Figma. TODO: ambil dari respons backend (field service_fee). */
internal const val ServiceFee = 5_000

/**
 * Bentuk draft menjadi data layar Ringkasan/Pembayaran.
 * Jika belum ada kategori (mis. layar dibuka lewat tombol demo di Beranda), pakai [CheckoutSummary.Sample].
 */
internal fun RegistrationDraft.toCheckoutSummary(): CheckoutSummary {
    if (serverEventId.isNotBlank()) {
        return CheckoutSummary(
            eventTitle = serverEventName,
            category = serverCategoryName,
            date = serverEventDate,
            location = serverEventLocation,
            participantName = participant.fullName,
            bibName = participant.bibName,
            jersey = participant.jersey,
            community = participant.community.ifBlank { null },
            registrationFee = serverCategoryPrice,
            serviceFee = 0,
            emergencyName = emergency.name,
            emergencyPhone = emergency.phone
        )
    }
    val event = baliTrailRun
    val category = event.categories.firstOrNull { it.distance == categoryDistance }
        ?: return CheckoutSummary.Sample
    return CheckoutSummary(
        eventTitle = event.title,
        category = category.distance,
        date = "${event.shortDate} ${event.month.substringAfter(' ')}",
        location = event.location.substringBefore(','),
        participantName = participant.fullName,
        bibName = participant.bibName,
        jersey = participant.jersey,
        community = participant.community.ifBlank { null },
        registrationFee = category.price,
        serviceFee = ServiceFee,
        emergencyName = emergency.name,
        emergencyPhone = emergency.phone
    )
}
