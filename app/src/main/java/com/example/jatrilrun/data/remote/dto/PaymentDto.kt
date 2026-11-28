package com.example.jatrilrun.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionListResponse(@SerializedName("data") val data: List<TransactionDto> = emptyList())
data class TransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("registration_id") val registrationId: String,
    @SerializedName("amount") val amount: Double? = null,
    @SerializedName("payment_status") val status: String? = null,
    @SerializedName("payment_method") val method: String? = null,
    @SerializedName("proof_path") val proofPath: String? = null,
    @SerializedName("expired") val expired: String? = null
)

data class UploadResponse(@SerializedName("data") val data: UploadedFileDto?)
data class UploadedFileDto(@SerializedName("relative_path") val relativePath: String)
data class SubmitPaymentRequest(
    @SerializedName("payment_method") val method: String,
    @SerializedName("proof_path") val proofPath: String
)
