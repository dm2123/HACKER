package com.example.hacker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hacker.domain.entities.Memory

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(val = true) val id: String,
    val category: String,
    val content: String,
    val importance: Int,
    val createdAt: Long
) {
    fun toDomain(): Memory {
        return Memory(
            id = id,
            category = category,
            content = content,
            importance = importance,
            createdAt = createdAt
        )
    }
}