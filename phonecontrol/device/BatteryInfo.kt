package com.example.hacker.phonecontrol.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.widget.Toast

/** Battery information and status */
class BatteryInfo(private val context: Context) {

    /** Get current battery level (0-100) */
    fun getBatteryLevel(): Int {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
    }

    /** Get battery scale (for percentage calculation) */
    fun getBatteryScale(): Int {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
    }

    /** Get battery percentage */
    fun getBatteryPercentage(): Int {
        val level = getBatteryLevel()
        val scale = getBatteryScale()
        return if (level >= 0 && scale > 0) (level * 100 / scale) else 0
    }

    /** Is charging */
    fun isCharging(): Boolean {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING
    }

    /** Get charging style */
    fun getChargingStyle(): Int {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_PLUG, -1) ?: 0
    }

    /** Is USB charging */
    fun isUsbCharging(): Boolean {
        return (getChargingStyle() and BatteryManager.BATTERY_PLUG_USB) != 0
    }

    /** Is AC charging */
    fun isAcCharging(): Boolean {
        return (getChargingStyle() and BatteryManager.BATTERY_PLUG_AC) != 0
    }

    /** Get battery temperature in Celsius */
    fun getTemperature(): Float {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getFloatExtra(BatteryManager.EXTRA_TEMPERATURE, 0f) ?: 0f
    }

    /** Get battery health */
    fun getHealth(): Int {
        val intent = context.registerReceiver(null, android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN) ?: BatteryManager.BATTERY_HEALTH_UNKNOWN
    }

    /** Is battery health good */
    fun isBatteryHealthGood(): Boolean {
        return getHealth() == BatteryManager.BATTERY_HEALTH_GOOD
    }

    /** Get battery status string */
    fun getBatteryStatusString(): String {
        return when (isCharging()) {
            true -> "Charging at ${getBatteryPercentage()}%"
            false -> "At ${getBatteryPercentage()}%"
        }
    }
}

/** Broadcast receiver for battery changes */
class BatteryChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level >= 0 && scale > 0) (level * 100 / scale) else 0
        
        // TODO: Emit event or update UI
    }
}