package com.example.hacker.services

import android.accessibilityservice.AccessibilityService
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.voice.VoiceInteractionService
import android.view.accessibility.AccessibilityEvent

class HackerVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
    }
}

class HackerNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Local-first notification handling (spec section 10). Active only after user grants access.
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // no-op
    }
}

class HackerAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Legitimate accessibility assistance only (spec section 11). Enabled by explicit user action.
    }

    override fun onInterrupt() {
        // no-op
    }
}
