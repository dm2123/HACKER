package com.example.hacker.data.repositories

import com.example.hacker.data.database.HackerDatabase
import com.example.hacker.data.database.ActivityLogEntity
import com.example.hacker.domain.entities.ActivityLog
import com.example.hacker.domain.repositories.ActivityLogRepository

class ActivityLogRepositoryImpl(private val database: HackerDatabase) : ActivityLogRepository {
    override fun getActivityLogs(): List<ActivityLog> {
        // TODO: Implement Room query
        return emptyList()
    }

    override fun logActivity(command: String, action: String, result: String, confirmationRequired: Boolean) {
        // TODO: Implement Room insert
    }

    override fun clearAllActivityLogs() {
        // TODO: Implement Room clear
    }
}