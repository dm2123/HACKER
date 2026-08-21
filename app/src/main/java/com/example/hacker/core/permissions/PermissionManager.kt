package com.example.hacker.core.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionManager {

    fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun allGranted(context: Context, permissions: List<String>): Boolean =
        permissions.all { isGranted(context, it) }

    fun missing(context: Context, permissions: List<String>): List<String> =
        permissions.filter { !isGranted(context, it) }
}
