package com.example.hacker.phonecontrol.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/** Clipboard manager */
class ClipboardManagerWrapper(private val context: Context) {
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    /** Get text from clipboard */
    fun getText(): String? {
        val clip = clipboardManager.primaryClip
        return if (clip != null && clip.getItemCount() > 0) {
            clip.getItemAt(0).coerceToText(context) as? String
        } else null
    }

    /** Set text to clipboard */
    fun setText(text: String) {
        val clip = ClipData.newPlainText("HACKER", text)
        clipboardManager.primaryClip = clip
    }
}