package com.example.hacker.voice

/**
 * HACKER personality (spec section 11).
 * Wraps action results into Yes Boss / failure / confirm lines.
 */
object ResponseEngine {

    fun success(detail: String): String {
        val clean = detail.trim()
        return if (clean.startsWith("Yes Boss", ignoreCase = true)) clean
        else "Yes Boss, $clean"
    }

    fun fail(reason: String): String {
        return "Boss, ye kaam complete nahi ho paya. $reason"
    }

    fun confirm(action: String): String {
        return "Boss, $action karne se pehle confirmation chahiye. Continue karun?"
    }

    fun done(): String = "Yes Boss, kaam complete ho gaya."
}
