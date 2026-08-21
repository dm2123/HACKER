package com.example.hacker.data

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Msg(
    val text: String,
    val isUser: Boolean,
    val time: String
)

object Conversation {
    val messages = mutableStateListOf<Msg>()

    fun add(text: String, isUser: Boolean) {
        val t = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        messages.add(Msg(text, isUser, t))
    }

    fun clear() = messages.clear()
}
