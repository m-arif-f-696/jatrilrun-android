package com.example.jatrilrun.ui.screens.payment

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jatrilrun.data.remote.api.ApiService
import com.example.jatrilrun.data.remote.dto.SubmitPaymentRequest
import com.example.jatrilrun.data.remote.dto.TransactionDto
import com.example.jatrilrun.ui.screens.event.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import javax.inject.Inject

sealed interface LivePaymentState {
    data object Loading : LivePaymentState
    data class Ready(val transaction: TransactionDto) : LivePaymentState
    data class Error(val message: String) : LivePaymentState
}

@HiltViewModel
class LivePaymentViewModel @Inject constructor(
    private val api: ApiService,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow<LivePaymentState>(LivePaymentState.Loading)
    val state = _state.asStateFlow()
    private val _proof = MutableStateFlow<Uri?>(null)
    val proof = _proof.asStateFlow()
    private val _method = MutableStateFlow("TRANSFER_BANK")
    val method = _method.asStateFlow()
    private var registrationId: String = ""

    fun start(id: String) {
        if (registrationId != id) registrationId = id
        refresh()
    }

    fun setProof(uri: Uri?) { _proof.value = uri }
    fun setMethod(value: String) { _method.value = value }

    fun refresh() = viewModelScope.launch {
        _state.value = LivePaymentState.Loading
        _state.value = try {
            val response = api.transactions()
            if (!response.isSuccessful) error("Transaksi gagal dimuat (${response.code()}).")
            val transaction = response.body()?.data?.firstOrNull { it.registrationId == registrationId }
                ?: error("Transaksi pendaftaran belum ditemukan di server.")
            LivePaymentState.Ready(transaction)
        } catch (error: Exception) {
            LivePaymentState.Error(error.localizedMessage ?: "Transaksi gagal dimuat.")
        }
    }

    fun submitProof() = viewModelScope.launch {
        val transaction = (_state.value as? LivePaymentState.Ready)?.transaction ?: return@launch
        val uri = _proof.value ?: return@launch
        _state.value = LivePaymentState.Loading
        try {
            val bytes = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } ?: error("Bukti pembayaran tidak dapat dibaca.")
            if (bytes.size > 10 * 1024 * 1024) error("Ukuran bukti maksimal 10 MB.")
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            if (mime !in setOf("image/jpeg", "image/png", "image/webp", "image/gif")) {
                error("Format bukti harus JPG, PNG, WEBP, atau GIF.")
            }
            val extension = when (mime) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                "image/gif" -> "gif"
                else -> "jpg"
            }
            val part = MultipartBody.Part.createFormData(
                "file", "bukti-pembayaran.$extension", bytes.toRequestBody(mime.toMediaTypeOrNull())
            )
            val uploaded = api.uploadProof(part, "photo".toRequestBody())
            if (!uploaded.isSuccessful) error("Bukti gagal diunggah (${uploaded.code()}).")
            val path = uploaded.body()?.data?.relativePath ?: error("Lokasi bukti tidak dikirim server.")
            val submitted = api.submitPayment(transaction.id, SubmitPaymentRequest(_method.value, path))
            if (!submitted.isSuccessful) error("Bukti ditolak server (${submitted.code()}). ${submitted.errorBody()?.string().orEmpty().take(240)}")
            _proof.value = null
            refresh()
        } catch (error: Exception) {
            _state.value = LivePaymentState.Error(error.localizedMessage ?: "Bukti gagal dikirim.")
        }
    }
}

@Composable
fun LivePaymentScreen(
    registrationId: String,
    onBack: () -> Unit,
    onHome: () -> Unit,
    viewModel: LivePaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val proof by viewModel.proof.collectAsState()
    val method by viewModel.method.collectAsState()
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.setProof(it) }
    LaunchedEffect(registrationId) { viewModel.start(registrationId) }

    EventScreen("Status pembayaran", onBack, bottomBar = {
        EventFooter("Kembali ke Beranda", onHome)
    }) {
        EventTitle("Pendaftaran tersimpan.")
        EventBody("Nomor registrasi: $registrationId")
        when (val current = state) {
            LivePaymentState.Loading -> EventCaption("Memeriksa transaksi di server...")
            is LivePaymentState.Error -> {
                EventNotice("Transaksi belum dapat ditampilkan", current.message, warning = true)
                EventAction("Coba lagi", { viewModel.refresh() }, secondary = true)
            }
            is LivePaymentState.Ready -> {
                val transaction = current.transaction
                EventPanel("Transaksi server") {
                    EventText("Total: ${rupiah(transaction.amount?.toInt() ?: 0)}")
                    EventText("Status: ${transaction.status ?: "PENDING"}")
                    transaction.expired?.let { EventCaption("Batas waktu: $it") }
                }
                when (transaction.status) {
                    "APPROVED" -> EventNotice("Pembayaran disetujui", "Tiket akan tersedia setelah server menerbitkannya.")
                    "REJECTED", "EXPIRED" -> EventNotice("Pembayaran tidak disetujui", "Periksa status pada website atau hubungi penyelenggara.", warning = true)
                    else -> if (transaction.proofPath != null) {
                        EventNotice("Menunggu verifikasi admin", "Bukti diterima server. Periksa status kembali nanti.")
                    } else {
                        EventNotice("Unggah bukti pembayaran", "Gunakan instruksi pembayaran resmi dari penyelenggara. Nomor VA pada alur demo bukan rekening pembayaran nyata.")
                        EventPanel("Metode pembayaran") {
                            EventAction("Transfer bank${if (method == "TRANSFER_BANK") " ✓" else ""}", { viewModel.setMethod("TRANSFER_BANK") }, secondary = true)
                            EventAction("QRIS${if (method == "QRIS") " ✓" else ""}", { viewModel.setMethod("QRIS") }, secondary = true)
                        }
                        EventAction("Pilih foto bukti", { picker.launch("image/*") }, secondary = true)
                        if (proof != null) EventCaption("Bukti dipilih: ${proof?.lastPathSegment.orEmpty()}")
                        EventAction("Kirim bukti ke server", { viewModel.submitProof() }, enabled = proof != null)
                    }
                }
                EventAction("Cek status pembayaran", { viewModel.refresh() }, secondary = true)
            }
        }
    }
}
