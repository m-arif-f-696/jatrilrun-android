package com.example.jatrilrun.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jatrilrun.ui.components.CommonTextField
import com.example.jatrilrun.ui.screens.checkout.EmergencyForm
import com.example.jatrilrun.ui.screens.checkout.ParticipantForm
import com.example.jatrilrun.ui.screens.checkout.RegistrationStepper
import com.example.jatrilrun.ui.screens.event.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ParticipantScreen(
    initial: ParticipantForm = ParticipantForm(),
    requiresServerProfile: Boolean = false,
    onBack: () -> Unit,
    onContinue: (ParticipantForm) -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf(initial.fullName) }
    var bibName by rememberSaveable { mutableStateOf(initial.bibName) }
    var email by rememberSaveable { mutableStateOf(initial.email) }
    var phone by rememberSaveable { mutableStateOf(initial.phone) }
    var birthDate by rememberSaveable { mutableStateOf(initial.birthDate) }
    var gender by rememberSaveable { mutableStateOf(initial.gender) }
    var jersey by rememberSaveable { mutableStateOf(initial.jersey) }
    var community by rememberSaveable { mutableStateOf(initial.community) }
    var idCard by rememberSaveable { mutableStateOf(initial.idCard) }
    var idCardType by rememberSaveable { mutableStateOf(initial.idCardType) }
    var bloodType by rememberSaveable { mutableStateOf(initial.bloodType) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val bibInvalid = bibName.isBlank() || bibName.length > 12
    val emailInvalid = !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val phoneInvalid = phone.isBlank() || (requiresServerProfile && phone.length > 15)
    val birthDateInvalid = birthDate.isBlank() || (requiresServerProfile && runCatching {
        SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).apply { isLenient = false }.parse(birthDate)
    }.getOrNull() == null)
    val invalidCount = listOf(fullName.isBlank(), bibInvalid, emailInvalid, phoneInvalid,
        birthDateInvalid, gender.isBlank(), jersey.isBlank(),
        requiresServerProfile && (idCard.isBlank() || idCard.length > 16),
        requiresServerProfile && idCardType.isBlank(),
        requiresServerProfile && bloodType.isBlank()).count { it }

    EventScreen("Data peserta", onBack, bottomBar = {
        EventFooter(if (submitted && invalidCount > 0) "Perbaiki & lanjutkan" else "Lanjut ke kontak darurat", {
            submitted = true
            if (invalidCount == 0) {
                onContinue(
                    ParticipantForm(
                        fullName.trim(), bibName.trim(), email.trim(), phone.trim(),
                        birthDate.trim(), gender, jersey, community.trim(), idCard.trim(), idCardType, bloodType
                    )
                )
            }
        })
    }) {
        RegistrationStepper(step = 1, label = "1 dari 4 · Data peserta")
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
            EventCaption(if (submitted && phoneInvalid) "Nomor wajib diisi, maksimal 15 karakter untuk server."
                else "Nomor aktif untuk informasi event.")
            CommonTextField(birthDate, { birthDate = it }, "Tanggal lahir (DD/MM/YYYY)")
            EventCaption(if (submitted && birthDateInvalid) "Gunakan tanggal valid dengan format DD/MM/YYYY."
                else "Usia minimum 18 tahun saat event.")
            ChoiceField("Jenis kelamin", listOf("Laki-laki", "Perempuan"), gender) { gender = it }
        }
        EventPanel("Info peserta & race kit") {
            ChoiceField("Ukuran jersey", listOf("S", "M", "L", "XL", "XXL"), jersey) { jersey = it }
            CommonTextField(community, { community = it }, "Komunitas (opsional)")
            if (requiresServerProfile) EventCaption("Nama BIB, jersey, dan komunitas belum didukung API pendaftaran; data ini hanya ada pada draft aplikasi.")
        }
        if (requiresServerProfile) EventPanel("Identitas wajib untuk profil runner") {
            EventBody("Backend memerlukan identitas dan golongan darah sebelum pendaftaran.")
            ChoiceField("Jenis identitas", listOf("KTP", "SIM"), idCardType) { idCardType = it }
            if (submitted && idCardType.isBlank()) EventCaption("Pilih jenis identitas.")
            CommonTextField(idCard, { idCard = it }, "Nomor identitas", isError = submitted && (idCard.isBlank() || idCard.length > 16))
            EventCaption("Maksimal 16 karakter.")
            ChoiceField("Golongan darah", listOf("A", "B", "AB", "O"), bloodType) { bloodType = it }
            if (submitted && bloodType.isBlank()) EventCaption("Pilih golongan darah.")
        }
    }
}

@Composable
fun EmergencyContactScreen(
    initial: EmergencyForm = EmergencyForm(),
    onBack: () -> Unit,
    onContinue: (EmergencyForm) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initial.name) }
    var relation by rememberSaveable { mutableStateOf(initial.relation) }
    var phone by rememberSaveable { mutableStateOf(initial.phone) }
    var medical by rememberSaveable { mutableStateOf(initial.medical) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    val nameInvalid = name.isBlank()
    val relationInvalid = relation.isBlank()
    val phoneInvalid = phone.filter(Char::isDigit).length < 10
    val invalid = nameInvalid || relationInvalid || phoneInvalid

    EventScreen("Kontak darurat", onBack, bottomBar = {
        EventFooter("Lanjut ke persetujuan", {
            submitted = true
            if (!invalid) onContinue(EmergencyForm(name.trim(), relation, phone.trim(), medical.trim()))
        })
    }) {
        RegistrationStepper(step = 1, label = "1 dari 4 · Kontak darurat")
        EventTitle("Ada yang bisa\nkami hubungi.")
        EventBody("Pilih orang yang tidak mengikuti lomba ini.")
        if (submitted && invalid) EventNotice(
            "Periksa kontak darurat",
            "Lengkapi kolom yang ditandai di bagian kontak. Informasi medis tetap opsional.",
            warning = true
        )
        EventPanel("Kontak yang bisa dihubungi") {
            CommonTextField(name, { name = it }, "Nama kontak", isError = submitted && nameInvalid)
            EventCaption(if (submitted && nameInvalid) "Nama kontak wajib diisi." else "Nama orang terdekat.")
            ChoiceField("Hubungan", listOf("Orang tua", "Pasangan", "Saudara", "Teman"), relation) { relation = it }
            if (submitted && relationInvalid) EventCaption("Pilih hubungan dengan kontak darurat.")
            CommonTextField(phone, { phone = it }, "Nomor WhatsApp", isError = submitted && phoneInvalid)
            EventCaption(if (submitted && phoneInvalid) "Nomor WhatsApp harus berisi minimal 10 digit."
                else "Pastikan nomor dapat dihubungi saat race.")
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
