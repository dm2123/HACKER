package com.example.hacker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hacker.domain.entities.ActivityLog

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(val = true) val id: String,
    val command: String,
    val action: String,
    val result: String,
    val timestamp: Long,
    val confirmationRequired: Boolean
) {
    fun toDomain(): ActivityLog {
        return ActivityLog(
            id = id,
            command = command,
            action = action,
            result = result,
            timestamp = timestamp,
            confirmationRequired = confirmationRequired
        )
    }
}