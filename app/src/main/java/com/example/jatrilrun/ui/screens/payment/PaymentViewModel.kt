package com.example.jatrilrun.ui.screens.payment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface PaymentUiState {
    object Idle : PaymentUiState
    object Success : PaymentUiState
}

@HiltViewModel
class PaymentViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _selectedMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedMethod: StateFlow<PaymentMethod?> = _selectedMethod.asStateFlow()

    fun selectMethod(method: PaymentMethod) {
        _selectedMethod.value = method
    }

    fun processPayment() {
        _uiState.value = PaymentUiState.Success
    }
}
