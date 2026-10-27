package com.example.jatrilrun.data.remote.api

import com.example.jatrilrun.data.remote.dto.AuthResponse
import com.example.jatrilrun.data.remote.dto.LoginRequest
import com.example.jatrilrun.data.remote.dto.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/logout")
    suspend fun logout(): Response<AuthResponse>
}
