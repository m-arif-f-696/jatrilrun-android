package com.example.jatrilrun.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun LoginScreen(
    selectedCategory: String? = null,
    allowDemo: Boolean = true,
    onBack: () -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onLoginSuccess()
    }

    EventScreen("Masuk", onBack, bottomBar = {
        EventFooter("Masuk & lanjutkan", {
            showErrors = true
            if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() && password.isNotBlank()) {
                viewModel.login(email.trim(), password)
            }
        }, enabled = uiState !is AuthUiState.Loading)
    }) {
        EventTitle("Larimu dimulai\ndari sini.")
        EventBody("Masuk untuk menyimpan data dan mengakses tiket.")
        if (selectedCategory != null) EventBadge("$selectedCategory · pilihan tersimpan")
        EventPanel("Masuk ke akunmu") {
            CommonTextField(email, { email = it }, "Email")
            EventCaption("Gunakan email yang aktif.")
            if (showErrors && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                EventCaption("Masukkan alamat email yang valid.")
            }
            CommonTextField(password, { password = it }, "Kata sandi", isPassword = true)
            EventCaption("Minimal 8 karakter.")
            if (showErrors && password.isBlank()) EventCaption("Kata sandi wajib diisi.")
        }
        if (uiState is AuthUiState.Loading) EventCaption("Memeriksa akun...")
        if (uiState is AuthUiState.Error) {
            EventNotice("Gagal masuk", (uiState as AuthUiState.Error).message, warning = true)
        }
        if (allowDemo) EventLink("Lanjutkan simulasi tanpa akun", onLoginSuccess, Modifier.fillMaxWidth())
        EventLink("Belum punya akun? Daftar", onNavigateToRegister, Modifier.fillMaxWidth())
        EventCaption("Dengan melanjutkan, Anda menyetujui kebijakan privasi Jatrilrun.")
    }
}
