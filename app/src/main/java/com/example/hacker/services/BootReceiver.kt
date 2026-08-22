package com.example.hacker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (HackerHotwordService.isEnabled(context)) {
                try {
                    val svc = Intent(context, HackerHotwordService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(svc)
                    else context.startService(svc)
                } catch (_: Exception) {}
            }
        }
    }
}
