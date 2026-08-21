package com.example.hacker.phonecontrol.apps

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/** Launches Android applications */
class AppLauncher(private val context: android.content.Context) {
    /** Launch an app by package name */
    fun launchApp(packageName: String): Boolean {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            }
            return false
        } catch (e: Exception) {
            return false
        }
    }

    /** Check if app is installed */
    fun isAppInstalled(packageName: String): Boolean {
        try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }
}