package com.example.hacker.ui

// Temporary stub file to fix build errors
// This file provides placeholder implementations for missing components

// Stub for NotificationEntity (until Room entity sync is complete)
data class NotificationEntity(
    val appLabel: String = "",
    val title: String = "",
    val text: String = "",
    val postedAt: Long = 0L
)

// Stub for NotificationRepository
class NotificationRepository(private val context: android.content.Context) {
    suspend fun getRecent(limit: Int): List<NotificationEntity> = emptyList()
    suspend fun clear() {}
}
