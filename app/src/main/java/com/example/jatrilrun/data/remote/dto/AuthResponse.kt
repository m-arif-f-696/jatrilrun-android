package com.example.jatrilrun.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: UserDto?
)
