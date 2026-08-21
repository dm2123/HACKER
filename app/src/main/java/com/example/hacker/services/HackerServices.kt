package com.example.hacker.services

import android.accessibilityservice.AccessibilityService
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.voice.VoiceInteractionService
import android.view.accessibility.AccessibilityEvent

/**
 * Voice Interaction Service (spec sections 4, 16).
 * Registered as the system voice interaction service.
 * When HACKER is set as the system assistant (ROLE_ASSISTANT),
 * this service handles assistant invocations.
 */
class HackerVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
    }
}

/**
 * Notification Listener (spec section 10).
 * Active only after user explicitly grants access in Settings.
 * Persists notifications locally (spec 10: local-first).
 */
class HackerNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName == packageName) return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString() ?: extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        if (title.isBlank() && text.isBlank()) return
        val label = try { packageManager.getApplicationLabel(packageManager.getApplicationInfo(sbn.packageName, 0)).toString() } catch(_:Exception){ sbn.packageName }
        com.example.hacker.data.repository.NotificationRepository.save(this, sbn.packageName, label, title, text)
    }
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}

/**
 * Accessibility Service (spec section 11).
 * Legitimate accessibility assistance only. Enabled by explicit user action.
 */
class HackerAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
