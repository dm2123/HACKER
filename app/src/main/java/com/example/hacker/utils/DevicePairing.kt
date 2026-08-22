package com.example.hacker.utils

/**
 * HACKER 6.0 - Device pairing state machine
 */
enum class PairingState {
    DISCOVERING,
    REQUESTING,
    AUTHENTICATING,
    PAIRED,
    REVOKED,
    ERROR
}

enum class DeviceType {
    PHONE, PC, TABLET, SMART_SPEAKER, SMART_HOME
}

enum class TrustLevel {
    FULL, LIMITED, REVOKED
}

/**
 * A paired device in the HACKER multi-device ecosystem.
 */
data class DeviceConnection(
    val deviceId: String,
    val deviceName: String,
    val deviceType: DeviceType,
    val trustLevel: TrustLevel,
    val pairingState: PairingState = PairingState.DISCOVERING,
    val connectedAt: Long = 0L,
    val signalStrength: Int = 0
)

/**
 * Per-feature access control for paired devices.
 */
data class FeatureAccess(
    val featureName: String,
    val isEnabled: Boolean,
    val requiresAuth: Boolean,
    val sandboxLevel: String = "standard"
)

/**
 * Result of a pairing attempt.
 */
sealed class PairingResult {

    data class Success(
        val device: DeviceConnection,
        val authToken: String,
        val expiresIn: Long
    ) : PairingResult()

    data class Cancelled(
        val reason: String
    ) : PairingResult()

    data class Failed(
        val errorCode: String,
        val message: String
    ) : PairingResult()
}
