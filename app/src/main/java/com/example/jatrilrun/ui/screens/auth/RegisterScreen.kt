package com.example.jatrilrun.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun RegisterScreen(
    allowDemo: Boolean = true,
    onBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    val validPassword = password.length >= 8 && password.any(Char::isLetter) && password.any(Char::isDigit)
    val uiState by viewModel.uiState.collectAsState()

    EventScreen("Daftar akun", onBack, bottomBar = {
        EventFooter("Buat akun & lanjutkan", {
            showErrors = true
            if (name.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() && validPassword && agreed) {
                viewModel.register(name.trim(), email.trim(), password)
            }
        }, enabled = uiState !is AuthUiState.Loading && uiState !is AuthUiState.Registered)
    }) {
        EventTitle("Satu akun untuk\nsetiap perjalanan.")
        EventPanel("Data akun") {
            CommonTextField(name, { name = it }, "Nama lengkap")
            EventCaption("Sesuai kartu identitas.")
            if (showErrors && name.isBlank()) EventCaption("Nama lengkap wajib diisi.")
            CommonTextField(email, { email = it }, "Email")
            EventCaption("Konfirmasi pendaftaran dikirim ke email ini.")
            if (showErrors && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                EventCaption("Masukkan alamat email yang valid.")
            }
            CommonTextField(password, { password = it }, "Kata sandi", isPassword = true)
            EventCaption("Minimal 8 karakter, huruf dan angka.")
            if (showErrors && !validPassword) EventCaption("Gunakan minimal 8 karakter, huruf dan angka.")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(agreed, { agreed = it })
            EventText("Saya menyetujui kebijakan privasi")
        }
        if (showErrors && !agreed) EventCaption("Persetujuan diperlukan untuk melanjutkan.")
        if (uiState is AuthUiState.Loading) EventCaption("Membuat akun...")
        if (uiState is AuthUiState.Error) {
            EventNotice("Gagal membuat akun", (uiState as AuthUiState.Error).message, warning = true)
        }
        if (uiState is AuthUiState.Registered) {
            EventNotice("Periksa email verifikasi", "Akun dibuat. Buka tautan verifikasi yang dikirim ke $email, lalu masuk untuk melanjutkan.")
        }
        if (allowDemo) EventLink("Lanjutkan simulasi tanpa membuat akun", onRegisterSuccess, Modifier.fillMaxWidth())
        EventLink("Sudah punya akun? Masuk", onNavigateToLogin, Modifier.fillMaxWidth())
    }
}
