package com.example.hacker.core.logging

import android.util.Log

/** Simple logging utility with different levels */
class HackerLogger {
    private const val TAG = "HACKER"
    
    fun d(tag: String, message: String) {
        Log.d(TAG, "$tag: $message")
    }
    
    fun i(tag: String, message: String) {
        Log.i(TAG, "$tag: $message")
    }
    
    fun w(tag: String, message: String) {
        Log.w(TAG, "$tag: $message")
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(TAG, "$tag: $message", throwable)
    }
    
    /** Log user command */
    fun logUserCommand(command: String) {
        d("COMMAND", command)
    }
    
    /** Log action execution */
    fun logActionExecution(action: String, result: String) {
        d("ACTION", "$action -> $result")
    }
}