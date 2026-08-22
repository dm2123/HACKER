package com.example.hacker.core.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

/** Permission manager with real Android checks */
class PermissionManager(private val context: Context) {

    /** Check if permission is granted (sync check, no UI) */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /** Request is UI-driven; this helper only reports current state.
     *  Actual request must be via ActivityResultContracts in Activity/Compose. */
    fun requestPermission(permission: String, requestCode: Int): Boolean {
        return isPermissionGranted(permission)
    }

    /** Open app settings for permission */
    fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallback = Intent(Settings.ACTION_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(fallback)
            } catch (_: Exception) {}
        }
    }
}
