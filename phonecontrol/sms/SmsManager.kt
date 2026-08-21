package com.example.hacker.phonecontrol.sms

import android.content.Intent
import android.net.Uri

/** SMS manager for sending/receiving messages */
class SmsManager(private val context: android.content.Context) {
    /** Compose a new SMS */
    fun composeSms(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            putExtra("sms_body", message)
            putExtra("address", phoneNumber)
            setType("vnd.android-dir/mms-sms")
        }
        context.startActivity(intent)
    }

    /** Send SMS */
    fun sendSms(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            putExtra("sms_body", message)
            data = Uri.parse("smsto:$phoneNumber")
        }
        context.startActivity(intent)
    }
}