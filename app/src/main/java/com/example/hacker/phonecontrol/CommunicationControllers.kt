package com.example.hacker.phonecontrol

import android.content.Context
import com.example.hacker.phone.DeviceActions

object Dialer {
    fun call(context: Context, number: String): String = try {
        DeviceActions.callNumber(context, number)
        "Calling $number"
    } catch (e: Exception) {
        DeviceActions.dialNumber(context, number)
        "Dialer opened for $number"
    }

    fun dial(context: Context) = DeviceActions.openDialer(context)
}

object SmsController {
    fun open(context: Context) = DeviceActions.openSmsApp(context)
    fun to(context: Context, number: String, body: String = "") = DeviceActions.sendSms(context, number, body)
}

object Contacts {
    fun lookupNumber(context: Context, name: String): String? = DeviceActions.contactNumber(context, name)
}
