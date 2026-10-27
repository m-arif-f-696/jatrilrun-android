package com.example.jatrilrun.domain.repository

import com.example.jatrilrun.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getLoggedInUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun logout()
}
