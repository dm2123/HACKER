package com.example.hacker.phonecontrol.dialer

import android.content.Intent
import android.net.Uri

/** Dialer for making calls */
class Dialer(private val context: android.content.Context) {
    /** Make a phone call */
    fun dialPhone(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    }

    /** Check if dialer is available */
    fun canDial(): Boolean {
        val intent = Intent(Intent.ACTION_DIAL)
        return intent.resolveActivity(context.packageManager) != null
    }
}