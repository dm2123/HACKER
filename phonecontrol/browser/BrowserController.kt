package com.example.hacker.phonecontrol.browser

import android.content.Intent
import android.net.Uri

/** Browser controller */
class BrowserController(private val context: android.content.Context) {
    /** Open a URL in browser */
    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Check if browser is available */
    fun canOpenUrl(): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
        return intent.resolveActivity(context.packageManager) != null
    }
}