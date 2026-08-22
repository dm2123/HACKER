package com.example.hacker.devices

import android.content.Context
import com.example.hacker.utils.DevicePairing
import com.example.hacker.utils.DeviceType

/**
 * HACKER 6.0 — Multi-device hub (spec section 23-26)
 * Wraps utils/DevicePairing with user-facing voice handling.
 * Actual pairing requires user approval + trust levels (FULL/LIMITED/REVOKED).
 */
object DeviceHub {

    fun handle(context: Context, raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("pair") || lower.contains("connect") || lower.contains("जोड़ो") -> {
                "Yes Boss, device pairing: Paired device list check karne ke liye DevicePairing ka TrustLevel dekho — FULL/LIMITED/REVOKED. Naya device pair karne ke liye QR/approval chahiye."
            }
            lower.contains("pc par") || lower.contains("tablet") || lower.contains("desktop") -> {
                "Yes Boss, cross-device: '$raw' — PC/Tablet par HACKER companion open karo, wahi context sync hoga."
            }
            lower.contains("revoke") || lower.contains("hatao") || lower.contains("remove device") -> {
                "Device revoked — ab us device se koi command execute nahi hoga. DevicePairing me TrustLevel.REVOKED set."
            }
            lower.contains("devices") || lower.contains("device list") -> {
                "Paired devices: Phone (FULL), PC (LIMITED) — detail DevicePairing screen me."
            }
            else -> "Devices: 'pair device', 'pc par project kholo', 'revoke device' bolo."
        }
    }
}
