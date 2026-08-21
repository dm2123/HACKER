package com.example.hacker.phonecontrol.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/** Reads and manages notifications */
class NotificationReader : NotificationListenerService() {

    /** Called when a notification is posted */
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        // TODO: Handle notification posted
    }

    /** Called when a notification is removed */
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // TODO: Handle notification removed
    }

    /** Get recent notifications */
    fun getRecentNotifications(): List<String> {
        // TODO: Implement
        return emptyList()
    }

    /** Get notification details */
    fun getNotificationDetails(sbn: StatusBarNotification): String {
        // TODO: Implement
        return ""
    }
}