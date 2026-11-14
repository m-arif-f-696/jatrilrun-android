package com.example.jatrilrun.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun LoginScreen(
    selectedCategory: String? = null,
    onBack: () -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    EventScreen("Masuk", onBack, bottomBar = {
        EventFooter("Masuk & lanjutkan", {
            showErrors = true
            if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() && password.isNotBlank()) {
                onLoginSuccess()
            }
        })
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
        EventCaption("Pratinjau tampilan: akun belum diverifikasi atau disimpan.")
        EventLink("Belum punya akun? Daftar", onNavigateToRegister, Modifier.fillMaxWidth())
        EventCaption("Dengan melanjutkan, Anda menyetujui kebijakan privasi Jatrilrun.")
    }
}
