package com.example.jatrilrun.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
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
fun RegisterScreen(
    onBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onRegisterSuccess()
        }
    }

    EventScreen("Daftar akun", onBack, bottomBar = {
        EventFooter("Buat akun & lanjutkan", { viewModel.register(name, email, password) },
            agreed && name.isNotBlank() && email.isNotBlank() && password.length >= 8 && uiState !is AuthUiState.Loading)
    }) {
        EventTitle("Satu akun untuk\nsetiap perjalanan.")
        EventPanel("Data akun") {
            CommonTextField(name, { name = it }, "Nama lengkap")
            EventCaption("Sesuai kartu identitas.")
            CommonTextField(email, { email = it }, "Email")
            EventCaption("Konfirmasi pendaftaran dikirim ke email ini.")
            CommonTextField(password, { password = it }, "Kata sandi", isPassword = true)
            EventCaption("Minimal 8 karakter, huruf dan angka.")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(agreed, { agreed = it })
            EventText("Saya menyetujui kebijakan privasi")
        }
        if (uiState is AuthUiState.Loading) LoadingIndicator()
        if (uiState is AuthUiState.Error) {
            EventNotice("Gagal membuat akun", (uiState as AuthUiState.Error).message, warning = true)
        }
        EventLink("Sudah punya akun? Masuk", onNavigateToLogin, Modifier.fillMaxWidth())
    }
}
