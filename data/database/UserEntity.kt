package com.example.hacker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hacker.domain.entities.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(val = true) val id: String,
    val name: String?,
    val language: String,
    val voiceProfileEnabled: Boolean,
    val memoryEnabled: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): User {
        return User(
            id = id,
            name = name,
            language = language,
            voiceProfileEnabled = voiceProfileEnabled,
            memoryEnabled = memoryEnabled,
            createdAt = createdAt
        )
    }
}