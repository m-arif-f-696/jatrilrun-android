package com.example.jatrilrun.ui.screens.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jatrilrun.ui.screens.checkout.CheckoutSummary
import com.example.jatrilrun.ui.screens.checkout.CostBreakdown
import com.example.jatrilrun.ui.screens.checkout.PaymentMethodItem
import com.example.jatrilrun.ui.screens.checkout.RegistrationStepper
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun PaymentScreen(
    onBack: () -> Unit,
    onPay: (PaymentMethod) -> Unit,
    summary: CheckoutSummary = CheckoutSummary.Sample,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val selected by viewModel.selectedMethod.collectAsState()

    EventScreen("Pembayaran", onBack, bottomBar = {
        EventFooter(
            label = if (selected != null) "Bayar ${rupiah(summary.total)}" else "Pilih metode untuk lanjut",
            onClick = {
                selected?.let {
                    viewModel.processPayment()
                    onPay(it)
                }
            },
            enabled = selected != null
        )
    }) {
        RegistrationStepper(step = 3, label = "3 dari 4 · Pembayaran")
        EventTitle("Bayar dengan\ncara pilihanmu.")
        PaymentMethod.entries.forEach { method ->
            PaymentMethodItem(
                title = method.title,
                note = "${method.description} · Biaya ${rupiah(summary.serviceFee)}",
                selected = selected == method,
                onClick = { viewModel.selectMethod(method) }
            )
        }
        CostBreakdown(summary)
        EventNotice(
            "Status pembayaran terlihat di aplikasi",
            "Selesaikan dalam 24 jam. Race pass aktif setelah pembayaran terkonfirmasi."
        )
    }
}
