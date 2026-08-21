package com.example.hacker.phonecontrol.calendar

import android.content.Intent
import java.util.Calendar

/** Calendar/event manager */
class CalendarManager(private val context: android.content.Context) {
    /** Create a calendar event */
    fun createEvent(title: String, description: String, startMillis: Long, endMillis: Long) {
        val intent = Intent(context, CalendarEventActivity::class.java).apply {
            putExtra("event_title", title)
            putExtra("event_description", description)
            putExtra("event_start", startMillis)
            putExtra("event_end", endMillis)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** View existing events */
    fun viewEvents() {
        val intent = Intent(context, CalendarViewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}