package com.example.jatrilrun.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var agreed by rememberSaveable { mutableStateOf(false) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    val validPassword = password.length >= 8 && password.any(Char::isLetter) && password.any(Char::isDigit)

    EventScreen("Daftar akun", onBack, bottomBar = {
        EventFooter("Buat akun & lanjutkan", {
            showErrors = true
            if (name.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() && validPassword && agreed) {
                onRegisterSuccess()
            }
        })
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
        EventCaption("Pratinjau tampilan: akun belum dibuat atau disimpan.")
        EventLink("Sudah punya akun? Masuk", onNavigateToLogin, Modifier.fillMaxWidth())
    }
}
