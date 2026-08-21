package com.example.hacker.core.security

/** Security level classification for actions */
enum class SecurityLevel {
    LOW,       // Open app, read battery, check time
    MODERATE,  // Create reminder, send message, change settings
    SENSITIVE  // Account changes, highly sensitive actions
}

/** Permission result for tool execution */
data class PermissionResult(
    val allowed: Boolean,
    val reason: String? = null,
    val requiresConfirmation: Boolean = false
)