package com.example.hacker.core.common

/** Result type for operations that can fail */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

/** Configuration preferences */
data class AppPreferences(
    var voiceWakeWord: String = "Hey HACKER",
    var language: String = "en",
    var memoryEnabled: Boolean = true,
    var privacyMode: Boolean = false
)