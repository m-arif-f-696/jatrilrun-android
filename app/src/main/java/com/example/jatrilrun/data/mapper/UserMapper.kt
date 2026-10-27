package com.example.jatrilrun.data.mapper

import com.example.jatrilrun.data.local.database.UserEntity
import com.example.jatrilrun.data.remote.dto.UserDto
import com.example.jatrilrun.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        token = token
    )
}

fun UserDto.toEntity(token: String?): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        token = token
    )
}

fun UserDto.toDomain(token: String?): User {
    return User(
        id = id,
        name = name,
        email = email,
        token = token
    )
}
