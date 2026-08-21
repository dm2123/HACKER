package com.example.hacker.core.common

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Failure(val message: String, val level: ActionLevel = ActionLevel.LOW) : AppResult<Nothing>()
}

enum class ActionLevel {
    LOW,      // direct execution
    MODERATE, // confirmation when appropriate
    SENSITIVE // strong confirmation / Android auth
}
