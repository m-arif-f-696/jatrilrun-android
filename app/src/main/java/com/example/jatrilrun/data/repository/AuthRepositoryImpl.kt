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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override fun getLoggedInUser(): Flow<User?> {
        return userDao.getUser().map { entity -> entity?.toDomain() }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            val body = response.body()
            if (response.isSuccessful && body != null && body.user != null) {
                val token = body.token ?: ""
                sessionManager.saveAuthToken(token)
                userDao.insertUser(body.user.toEntity(token))
                Result.success(body.user.toDomain(token))
            } else {
                Result.failure(Exception(body?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            val body = response.body()
            if (response.isSuccessful && body != null && body.user != null) {
                val token = body.token ?: ""
                sessionManager.saveAuthToken(token)
                userDao.insertUser(body.user.toEntity(token))
                Result.success(body.user.toDomain(token))
            } else {
                Result.failure(Exception(body?.message ?: "Register failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        userDao.clearUser()
        sessionManager.clearSession()
    }
}
