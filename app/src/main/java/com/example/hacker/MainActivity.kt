package com.example.hacker

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.hacker.ui.HackerApp
import com.example.hacker.ui.HackerTheme
import com.example.hacker.ui.rememberPermissionRequester

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                    }.toTypedArray()
                    if (need.isNotEmpty()) requester(need)
                }
                HackerApp()
            }
        }
    }
}
