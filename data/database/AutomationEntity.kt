package com.example.hacker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hacker.domain.entities.Automation

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey(val = true) val id: String,
    val name: String,
    val trigger: String,
    val actions: String,
    val enabled: Boolean,
    val createdAt: Long
) {
    fun toDomain(): Automation {
        return Automation(
            id = id,
            name = name,
            trigger = trigger,
            actions = actions,
            enabled = enabled,
            createdAt = createdAt
        )
    }
}