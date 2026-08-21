package com.example.hacker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.hacker.ui.HackerApp
import com.example.hacker.ui.rememberPermissionRequester
import com.example.hacker.ui.theme.HackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lock-screen support (spec §16)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            HackerTheme {
                val requester = rememberPermissionRequester()
                LaunchedEffect(Unit) {
                    val need = arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_CONTACTS
                    ).filter {
                        ContextCompat.checkSelfPermission(this@MainActivity, it) !=
                            PackageManager.PERMISSION_GRANTED
                    }.toTypedArray()
                    if (need.isNotEmpty()) requester(need)
                }
                HackerApp()
            }
        }
    }
}
