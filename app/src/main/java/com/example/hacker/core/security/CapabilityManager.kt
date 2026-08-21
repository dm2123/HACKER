package com.example.hacker.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

/**
 * Detects whether a given capability is supported on the current device / API level.
 * Per spec section 32: never assume a feature exists; detect and fall back.
 */
object CapabilityManager {

    fun supportsBluetoothPanel(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun supportsInternetConnectivityPanel(context: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun supportsFlashlight(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun supportsVoiceInteraction(context: Context): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_RECOGNITION)
}
