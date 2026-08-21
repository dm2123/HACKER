package com.example.hacker.phonecontrol

import android.content.Context
import com.example.hacker.phone.DeviceActions

/**
 * Tool Router facade (spec section 14).
 * Maps an internal action key + parameter to the correct controller.
 */
object PhoneControl {

    fun handle(context: Context, action: String, param: String): String {
        return when (action) {
            "open_app" -> if (AppLauncher.open(context, param)) "$param opened" else "$param not found"
            "torch" -> { TorchController.toggle(context); "Torch toggled" }
            "volume_up" -> { VolumeController.up(context); "Volume increased" }
            "volume_down" -> { VolumeController.down(context); "Volume decreased" }
            "wifi" -> { SettingsLauncher.wifi(context); "WiFi settings opened" }
            "bluetooth" -> { SettingsLauncher.bluetooth(context); "Bluetooth settings opened" }
            "call" -> Dialer.call(context, param)
            "sms" -> { SmsController.open(context); "SMS app opened" }
            "alarm" -> { AlarmController.setAlarm(context, 6, 0); "Alarm set for 06:00" }
            "web" -> { DeviceActions.webSearch(context, param); "Searching the web" }
            "youtube" -> { DeviceActions.youtubeSearch(context, param); "Opening YouTube" }
            else -> "Unknown action: $action"
        }
    }
}
