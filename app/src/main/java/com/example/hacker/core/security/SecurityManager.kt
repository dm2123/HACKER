package com.example.hacker.core.security

import android.content.Context
import com.example.hacker.data.preferences.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * Security gate (spec section 6, 30).
 * Sensitive actions (call/SMS) require a PIN when security mode is enabled.
 * PIN is stored as SHA-256 hash in DataStore — never plaintext.
 */
object SecurityManager {

    fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }

    suspend fun isPinSet(context: Context): Boolean =
        UserPreferences.securityPinHash(context).first().isNotEmpty()

    suspend fun isSecurityEnabled(context: Context): Boolean =
        UserPreferences.securityEnabled(context).first()

    suspend fun setPin(context: Context, pin: String) {
        if (pin.isBlank()) {
            UserPreferences.setSecurityPinHash(context, "")
            UserPreferences.setSecurityEnabled(context, false)
        } else {
            UserPreferences.setSecurityPinHash(context, sha256(pin))
        }
    }

    suspend fun verifyPin(context: Context, pin: String): Boolean {
        val stored = UserPreferences.securityPinHash(context).first()
        return stored.isNotEmpty() && stored == sha256(pin)
    }

    /**
     * Returns true if the action may proceed.
     * When security is enabled and a PIN is set, sensitive actions are blocked
     * until unlocked via the Security screen or voice PIN.
     */
    suspend fun canPerformSensitive(context: Context): Boolean {
        val pinSet = isPinSet(context)
        val enabled = isSecurityEnabled(context)
        return !(pinSet && enabled)
    }

    fun setSecurityEnabledAsync(context: Context, enabled: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            UserPreferences.setSecurityEnabled(context, enabled)
        }
    }
}
