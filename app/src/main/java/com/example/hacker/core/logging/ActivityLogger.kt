package com.example.hacker.core.logging

interface ActivityLogger {
    fun log(command: String, action: String, result: String, confirmationRequired: Boolean = false)
}
