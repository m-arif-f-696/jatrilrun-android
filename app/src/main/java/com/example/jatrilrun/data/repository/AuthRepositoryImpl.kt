package com.example.jatrilrun.data.repository

import com.example.jatrilrun.data.local.database.UserDao
import com.example.jatrilrun.data.local.datastore.SessionManager
import com.example.jatrilrun.data.mapper.toDomain
import com.example.jatrilrun.data.mapper.toEntity
import com.example.jatrilrun.data.remote.api.ApiService
import com.example.jatrilrun.data.remote.dto.LoginRequest
import com.example.jatrilrun.data.remote.dto.RegisterRequest
import com.example.jatrilrun.domain.model.User
import com.example.jatrilrun.domain.repository.AuthRepository
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.CookieJar
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val sessionManager: SessionManager,
    private val cookieJar: CookieJar
) : AuthRepository {

    override fun getLoggedInUser(): Flow<User?> = userDao.getUser().map { it?.toDomain() }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = apiService.login(LoginRequest(email, password))
        if (!response.isSuccessful) throw backendError(response, "Gagal masuk")

        // Laravel Sanctum memakai cookie sesi, bukan token pada body respons.
        val session = apiService.session()
        if (!session.isSuccessful) {
            throw backendError(session, "Sesi tidak tersimpan. Periksa konfigurasi Sanctum pada server.")
        }
        val user = requireNotNull(session.body()?.data ?: response.body()?.data) {
            "Respons akun dari server tidak lengkap."
        }
        userDao.insertUser(user.toEntity(null))
        user.toDomain(null)
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> = runCatching {
        val response = apiService.register(RegisterRequest(name, email, password))
        if (!response.isSuccessful) throw backendError(response, "Gagal membuat akun")
        // Signup mengirim email verifikasi; belum ada sesi hingga pengguna verifikasi lalu masuk.
        val user = requireNotNull(response.body()?.data) { "Respons pendaftaran dari server tidak lengkap." }
        user.toDomain(null)
    }

    override suspend fun refreshSession() {
        try {
            val response = apiService.session()
            when {
                response.isSuccessful && response.body()?.data != null -> {
                    userDao.insertUser(requireNotNull(response.body()?.data).toEntity(null))
                }
                response.code() == 401 -> {
                    (cookieJar as? PersistentCookieJar)?.clear()
                    userDao.clearUser()
                }
            }
        } catch (_: Exception) {
            // Kegagalan jaringan tidak menghapus profil lokal; tindakan server tetap memeriksa sesi.
        }
    }

    override suspend fun logout() {
        try { apiService.logout() } catch (_: Exception) { /* tetap hapus sesi lokal */ }
        (cookieJar as? PersistentCookieJar)?.clear()
        userDao.clearUser()
        sessionManager.clearSession()
    }
}

private fun backendError(response: Response<*>, fallback: String): IllegalStateException {
    val message = runCatching {
        val json = JsonParser.parseString(response.errorBody()?.string().orEmpty()).asJsonObject
        json.get("message")?.asString
            ?: json.getAsJsonObject("errors")?.entrySet()?.firstOrNull()?.value?.asJsonArray?.firstOrNull()?.asString
    }.getOrNull()
    return IllegalStateException(message?.takeIf { it.isNotBlank() } ?: "$fallback (${response.code()})")
}
