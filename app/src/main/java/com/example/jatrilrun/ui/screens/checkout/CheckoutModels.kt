package com.example.jatrilrun.ui.screens.checkout

/**
 * Data ringkasan pendaftaran untuk layar Ringkasan & Pembayaran.
 * TODO: isi dari state peserta / kontak darurat / kategori yang dipilih (sekarang masih [Sample] dari Figma).
 */
data class CheckoutSummary(
    val eventTitle: String,
    val category: String,
    val date: String,
    val location: String,
    val participantName: String,
    val bibName: String,
    val jersey: String,
    val community: String?,
    val registrationFee: Int,
    val serviceFee: Int,
    val emergencyName: String,
    val emergencyPhone: String
) {
    val total: Int get() = registrationFee + serviceFee

    companion object {
        val Sample = CheckoutSummary(
            eventTitle = "Bali Trail Run 2026",
            category = "30K",
            date = "15 Nov 2026",
            location = "Kintamani",
            participantName = "Iqbal Pratama",
            bibName = "IQBAL",
            jersey = "M",
            community = "Kampus Runners",
            registrationFee = 450_000,
            serviceFee = 5_000,
            emergencyName = "Rina Pratama",
            emergencyPhone = "0812 1234 5678"
        )
    }
}
