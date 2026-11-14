package com.example.jatrilrun.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.screens.event.*

@Composable
fun ParticipantScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var bibName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var jersey by rememberSaveable { mutableStateOf("") }
    var community by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val bibInvalid = bibName.isBlank() || bibName.length > 12
    val emailInvalid = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val invalidCount = listOf(fullName.isBlank(), bibInvalid, emailInvalid, phone.isBlank(),
        birthDate.isBlank(), gender.isBlank(), jersey.isBlank()).count { it }

    EventScreen("Data peserta", onBack, bottomBar = {
        EventFooter(if (submitted && invalidCount > 0) "Perbaiki & lanjutkan" else "Lanjut ke kontak darurat", {
            submitted = true
            if (invalidCount == 0) onContinue()
        })
    }) {
        EventPanel { EventCaption("1 dari 4 · Data peserta") }
        EventTitle("Kenalkan dirimu.")
        EventBody("Data ini digunakan untuk BIB, jersey, dan konfirmasi event.")
        if (submitted && invalidCount > 0) {
            EventNotice("Ada $invalidCount data yang perlu diperiksa",
                "Periksa kolom yang ditandai. Data lain tetap tersimpan.", warning = true)
        }
        EventPanel("Identitas runner") {
            CommonTextField(fullName, { fullName = it }, "Nama lengkap")
            EventCaption("Sesuai kartu identitas.")
            CommonTextField(bibName, { bibName = it }, "Nama pada BIB")
            EventCaption(if (submitted && bibInvalid) "Nama BIB wajib diisi, maksimal 12 karakter."
                else "Maksimal 12 karakter, tanpa emoji.")
        }
        EventPanel("Kontak peserta") {
            CommonTextField(email, { email = it }, "Email")
            EventCaption(if (submitted && emailInvalid) "Masukkan alamat email yang valid."
                else "Konfirmasi dan tiket dikirim ke sini.")
            CommonTextField(phone, { phone = it }, "Nomor WhatsApp")
            EventCaption("Nomor aktif untuk informasi event.")
            CommonTextField(birthDate, { birthDate = it }, "Tanggal lahir (DD/MM/YYYY)")
            EventCaption("Usia minimum 18 tahun saat event.")
            ChoiceField("Jenis kelamin", listOf("Laki-laki", "Perempuan"), gender) { gender = it }
        }
        EventPanel("Info peserta & race kit") {
            ChoiceField("Ukuran jersey", listOf("S", "M", "L", "XL", "XXL"), jersey) { jersey = it }
            CommonTextField(community, { community = it }, "Komunitas (opsional)")
        }
    }
}

@Composable
fun EmergencyContactScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var relation by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var medical by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val invalid = name.isBlank() || relation.isBlank() || phone.filter(Char::isDigit).length < 10

    EventScreen("Kontak darurat", onBack, bottomBar = {
        EventFooter("Lanjut ke persetujuan", {
            submitted = true
            if (!invalid) onContinue()
        })
    }) {
        EventPanel { EventCaption("1 dari 4 · Kontak darurat") }
        EventTitle("Ada yang bisa\nkami hubungi.")
        EventBody("Pilih orang yang tidak mengikuti lomba ini.")
        if (submitted && invalid) EventNotice("Periksa kontak darurat",
            "Isi nama, hubungan, dan nomor WhatsApp yang dapat dihubungi.", warning = true)
        EventPanel("Kontak yang bisa dihubungi") {
            CommonTextField(name, { name = it }, "Nama kontak")
            EventCaption("Nama orang terdekat.")
            ChoiceField("Hubungan", listOf("Orang tua", "Pasangan", "Saudara", "Teman"), relation) { relation = it }
            CommonTextField(phone, { phone = it }, "Nomor WhatsApp")
            EventCaption("Pastikan nomor dapat dihubungi saat race.")
        }
        EventPanel("Informasi medis") {
            CommonTextField(medical, { medical = it }, "Kondisi medis / alergi (opsional)")
            EventCaption("Membantu tim medis merespons dengan tepat.")
        }
        EventNotice("Data untuk keselamatan Anda",
            "Kontak ini diakses tim medis bila terjadi keadaan darurat.")
    }
}

@Composable
private fun ChoiceField(label: String, options: List<String>, value: String, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            EventText("$label · ${value.ifBlank { "Pilih" }}")
        }
        DropdownMenu(expanded, { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onChange(option)
                    expanded = false
                })
            }
        }
    }
}
