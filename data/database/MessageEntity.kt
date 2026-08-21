package com.example.hacker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hacker.domain.entities.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(val = true) val id: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long
) {
    fun toDomain(): Message {
        return Message(
            id = id,
            conversationId = conversationId,
            role = role,
            content = content,
            timestamp = timestamp
        )
    }
}