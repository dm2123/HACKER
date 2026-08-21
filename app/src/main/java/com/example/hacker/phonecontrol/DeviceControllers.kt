package com.example.hacker.phonecontrol

import android.content.Context
import com.example.hacker.phone.DeviceActions

object AppLauncher {
    fun open(context: Context, query: String): Boolean = DeviceActions.openApp(context, query)
}

object TorchController {
    fun toggle(context: Context): Boolean = DeviceActions.toggleTorch(context)
    fun isOn(context: Context): Boolean = DeviceActions.isTorchOn()
}

object VolumeController {
    fun up(context: Context) = DeviceActions.volumeUp(context)
    fun down(context: Context) = DeviceActions.volumeDown(context)
}

object DeviceInfo {
    fun batteryLevel(context: Context): Int = DeviceActions.batteryLevel(context)
    fun batteryStatus(context: Context): String = DeviceActions.batteryStatus(context)
}

object SettingsLauncher {
    fun wifi(context: Context) = DeviceActions.openWifi(context)
    fun bluetooth(context: Context) = DeviceActions.openBluetooth(context)
}
