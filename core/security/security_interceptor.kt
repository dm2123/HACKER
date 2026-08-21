package com.example.hacker.core.security

import com.example.hacker.core.security.SecurityLevel

/** Checks if an action is allowed based on security level */
class SecurityInterceptor {
    fun checkSecurity(level: SecurityLevel): PermissionResult {
        return when (level) {
            SecurityLevel.LOW -> PermissionResult(allowed = true)
            SecurityLevel.MODERATE -> PermissionResult(allowed = true, requiresConfirmation = true)
            SecurityLevel.SENSITIVE -> PermissionResult(allowed = false, reason = "Requires Android authentication")
        }
    }
}