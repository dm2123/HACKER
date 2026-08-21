package com.example.hacker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hacker.domain.entities.Conversation

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(val = true) val id: String,
    val title: String?,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Conversation {
        return Conversation(
            id = id,
            title = title,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}