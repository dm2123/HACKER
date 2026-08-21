package com.example.hacker.phonecontrol.settings

import android.content.Intent
import android.provider.Settings

/** Settings launcher */
class SettingsLauncher(private val context: android.content.Context) {
    /** Open a specific settings page */
    fun openSettings(page: String) {
        val intent = when (page) {
            "wifi" -> {
                Intent(Settings.ACTION_WIFI_SETTINGS)
            }
            "bluetooth" -> {
                Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            }
            "display" -> {
                Intent(Settings.ACTION_DISPLAY_SETTINGS)
            }
            "sound" -> {
                Intent(Settings.SOUND_SETTINGS)
            }
            "battery" -> {
                Intent(Settings.ENERGY_USAGE_SETTINGS)
            }
            "security" -> {
                Intent(Settings.ACTION_SECURITY_SETTINGS)
            }
            "general" -> {
                Intent(Settings.ACTION_SETTINGS)
            }
            else -> Intent(Settings.ACTION_SETTINGS)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Check if setting can be opened */
    fun canOpen(page: String): Boolean {
        val intent = when (page) {
            "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
            "bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            else -> Intent(Settings.ACTION_SETTINGS)
        }
        return intent.resolveActivity(context.packageManager) != null
    }
}