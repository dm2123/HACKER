package com.example.hacker.phonecontrol.device

import android.os.Build
import android.util.DisplayMetrics

/** Device information */
class DeviceInfo {

    /** Get device model */
    fun getModel(): String {
        return Build.MODEL
    }

    /** Get device manufacturer */
    fun getManufacturer(): String {
        return Build.MANUFACTURER
    }

    /** Get Android version */
    fun getAndroidVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    /** Get API level */
    fun getApiLevel(): Int {
        return Build.VERSION_CODES.SDK_INT
    }

    /** Get screen size */
    fun getScreenWidth(dp: Boolean = false): Int {
        if (dp) {
            // TODO: Return width in dp
            return 0
        }
        return 0
    }

    /** Get screen height */
    fun getScreenHeight(dp: Boolean = false): Int {
        if (dp) {
            // TODO: Return height in dp
            return 0
        }
        return 0
    }
}