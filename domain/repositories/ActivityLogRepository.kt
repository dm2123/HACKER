package com.example.hacker.domain.repositories

import com.example.hacker.domain.entities.ActivityLog

abstract class ActivityLogRepository {
    abstract fun getActivityLogs(): List<ActivityLog>
    abstract fun logActivity(command: String, action: String, result: String, confirmationRequired: Boolean)
    abstract fun clearAllActivityLogs()
}