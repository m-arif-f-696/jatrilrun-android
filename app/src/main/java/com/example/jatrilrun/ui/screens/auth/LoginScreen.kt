package com.example.jatrilrun.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jatrilrun.ui.components.CommonButton
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.components.LoadingIndicator
import com.example.jatrilrun.ui.screens.event.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LoginScreen(
    selectedCategory: String? = null,
    onBack: () -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    EventScreen("Masuk", onBack, bottomBar = {
        EventFooter("Masuk & lanjutkan", { viewModel.login(email, password) }, uiState !is AuthUiState.Loading)
    }) {
        EventTitle("Larimu dimulai\ndari sini.")
        EventBody("Masuk untuk menyimpan data dan mengakses tiket.")
        if (selectedCategory != null) EventBadge("$selectedCategory · pilihan tersimpan")
        EventPanel("Masuk ke akunmu") {
            CommonTextField(email, { email = it }, "Email")
            EventCaption("Gunakan email yang aktif.")
            CommonTextField(password, { password = it }, "Kata sandi", isPassword = true)
            EventCaption("Minimal 8 karakter.")
        }
        if (uiState is AuthUiState.Loading) LoadingIndicator()
        if (uiState is AuthUiState.Error) {
            EventNotice("Gagal masuk", (uiState as AuthUiState.Error).message, warning = true)
        }
        EventLink("Belum punya akun? Daftar", onNavigateToRegister, Modifier.fillMaxWidth())
        EventCaption("Dengan melanjutkan, Anda menyetujui kebijakan privasi Jatrilrun.")
    }
}
