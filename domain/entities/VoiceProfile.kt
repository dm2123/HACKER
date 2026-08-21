package com.example.hacker.domain.entities

import java.util.Date

/** Structured intent from natural language parsing */
data class IntentEntity(
    val intent: String,
    val entities: Map<String, Any>,
    val confidence: Float = 1.0f
)

/** Voice profile for speaker verification */
data class VoiceProfile(
    val id: String,
    val isEnrolled: Boolean,
    val voicePrintHash: String?,
    val createdAt: Long
)

/** User entity from database */
data class User(
    val id: String,
    val name: String?,
    val language: String,
    val voiceProfileEnabled: Boolean,
    val memoryEnabled: Boolean,
    val createdAt: Long
)

/** Conversation entity */
data class Conversation(
    val id: String,
    val title: String?,
    val createdAt: Long,
    val updatedAt: Long
)

/** Message entity */
data class Message(
    val id: String,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long
)

/** Memory entity */
data class Memory(
    val id: String,
    val category: String,
    val content: String,
    val importance: Int,
    val createdAt: Long
)

/** Automation entity */
data class Automation(
    val id: String,
    val name: String,
    val trigger: String,
    val actions: String,
    val enabled: Boolean,
    val createdAt: Long
)

/** Activity log entity */
data class ActivityLog(
    val id: String,
    val command: String,
    val action: String,
    val result: String,
    val timestamp: Long,
    val confirmationRequired: Boolean
)