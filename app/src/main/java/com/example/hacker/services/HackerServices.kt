package com.example.hacker.services

import android.accessibilityservice.AccessibilityService
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionService
import android.view.accessibility.AccessibilityEvent
import com.example.hacker.MainActivity

/**
 * Hack VoiceInteractionService (spec §4, §16).
 * When HACKER is set as the system assistant, the system routes assistant invocations
 * (power button / swipe gesture) to this service. The session immediately opens
 * MainActivity over the lock screen with voice ready.
 */
class HackerVoiceInteractionService : VoiceInteractionService() {

    override fun onReady() {
        super.onReady()
    }

    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        return HackerVoiceSession(this)
    }

    /**
     * Simple session that opens the main HACKER activity.
     * The activity has setShowWhenLocked(true) so it appears even when locked.
     */
    private class HackerVoiceSession(context: Context) : VoiceInteractionSession(context) {

        override fun onShow(args: Bundle?, showFlags: Int) {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
            finish()
        }

        override fun onHide() {
            // no-op
        }
    }
}

/**
 * Notification Listener (spec §10).
 * Active only after user explicitly grants access in Settings.
 */
class HackerNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Local-first notification handling. No cloud processing.
    }
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}

/**
 * Accessibility Service (spec §11).
 * Legitimate accessibility assistance only. Enabled by explicit user action.
 */
class HackerAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
