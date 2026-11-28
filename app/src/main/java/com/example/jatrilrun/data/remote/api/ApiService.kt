package com.example.jatrilrun.data.remote.api

import com.example.jatrilrun.data.remote.dto.AuthResponse
import com.example.jatrilrun.data.remote.dto.LoginRequest
import com.example.jatrilrun.data.remote.dto.RegisterRequest
import com.example.jatrilrun.data.remote.dto.EventListResponse
import com.example.jatrilrun.data.remote.dto.EventDetailResponse
import com.example.jatrilrun.data.remote.dto.ProfileRequest
import com.example.jatrilrun.data.remote.dto.RegistrationRequest
import com.example.jatrilrun.data.remote.dto.RegistrationResponse
import com.example.jatrilrun.data.remote.dto.RegistrationListResponse
import com.example.jatrilrun.data.remote.dto.TicketResponse
import com.example.jatrilrun.data.remote.dto.TransactionListResponse
import com.example.jatrilrun.data.remote.dto.UploadResponse
import com.example.jatrilrun.data.remote.dto.SubmitPaymentRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PATCH
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    @POST("api/auth/signin")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/signup")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/auth/session")
    suspend fun session(): Response<AuthResponse>

    @POST("api/auth/signout")
    suspend fun logout(): Response<ResponseBody>

    @GET("api/events")
    suspend fun events(): Response<EventListResponse>

    @GET("api/events/{slug}")
    suspend fun event(@Path("slug") slug: String): Response<EventDetailResponse>

    @GET("api/profiles/me")
    suspend fun profile(): Response<ResponseBody>

    @POST("api/profiles/me")
    suspend fun createProfile(@Body request: ProfileRequest): Response<ResponseBody>

    @PATCH("api/profiles/me")
    suspend fun updateProfile(@Body request: ProfileRequest): Response<ResponseBody>

    @POST("api/registrations")
    suspend fun createRegistration(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @GET("api/registrations")
    suspend fun registrations(): Response<RegistrationListResponse>

    @GET("api/registrations/{id}/ticket")
    suspend fun ticket(@Path("id") id: String): Response<TicketResponse>

    @GET("api/transactions")
    suspend fun transactions(): Response<TransactionListResponse>

    @Multipart
    @POST("api/media/upload")
    suspend fun uploadProof(@Part file: MultipartBody.Part, @Part("type") type: RequestBody): Response<UploadResponse>

    @POST("api/transactions/{id}/pay")
    suspend fun submitPayment(@Path("id") id: String, @Body request: SubmitPaymentRequest): Response<ResponseBody>
}
