package com.example.jatrilrun.ui.screens.payment

enum class PaymentMethod(val title: String, val description: String, val isVirtualAccount: Boolean) {
    BCA("BCA Virtual Account", "Verifikasi otomatis", true),
    BNI("BNI Virtual Account", "Verifikasi otomatis", true),
    QRIS("QRIS", "Bank & dompet digital", false)
}

/** DEMO: nomor VA dari Figma. Ganti dengan respons backend saat pembayaran dibuat. */
internal const val DemoVaNumber = "8808 2600 0030 48"
