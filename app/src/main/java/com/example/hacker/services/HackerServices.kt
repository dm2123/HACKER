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
 */
class HackerNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {}
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
