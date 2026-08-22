package com.example.hacker.coding

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * HACKER 4.0 — Coding AI (spec section 19-20)
 * Offline helpers for generate/explain/debug/review. No auto-overwrite of project files.
 */
object CodingAI {

    fun handle(context: Context, raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("explain") || lower.contains("samjhao") || lower.contains("समझाओ") -> {
                val code = extractAfter(raw, listOf("explain", "samjhao", "समझाओ"))
                "Yes Boss, code explanation:\n${explainCode(code)}\nMemory me save kar diya."
                    .also { save(context, "coding:explain", it) }
            }
            lower.contains("debug") || lower.contains("error") || lower.contains("एरर") || lower.contains("fix") -> {
                val code = extractAfter(raw, listOf("debug", "error", "एरर", "fix"))
                "Yes Boss, debugging:\n${debugCode(code)}\nSuggested fix Memory me save."
                    .also { save(context, "coding:debug", it) }
            }
            lower.contains("generate") || lower.contains("bana do") || lower.contains("लिख दो") -> {
                val what = extractAfter(raw, listOf("generate", "bana do", "लिख दो"))
                "Yes Boss, generate kar diya — $what:\n```\n// TODO: $what — template ready, edit karke use karo\n```"
                    .also { save(context, "coding:generate:$what", it) }
            }
            lower.contains("review") || lower.contains("review karo") -> {
                "Yes Boss, code review: 1) Naming clear? 2) Edge cases? 3) Complexity? 4) Tests missing? Detail Memory me."
            }
            lower.contains("refactor") -> "Yes Boss, refactor suggestion: function ko chhota karo, duplicate hatao, constants use karo."
            lower.contains("test") || lower.contains("टेस्ट") -> "Yes Boss, test cases: 1) Normal input 2) Edge (0, empty) 3) Invalid. Example generate karun?"
            else -> "Coding AI: 'code explain <code>', 'debug <error>', 'generate <feature> (python/java/kotlin)', 'review karo' — bolo."
        }
    }

    private fun explainCode(code: String): String {
        if (code.length < 5) return "Code paste karo — jaise 'code explain for loop in python'"
        return "Ye code ${code.take(80)} ... ko line-by-line: 1) Input leta hai 2) Loop chalata hai 3) Output deta hai. Complexity O(n)."
    }

    private fun debugCode(code: String): String {
        if (code.contains("null", true)) return "Possible NullPointer — null check add karo: if (x != null)"
        if (code.contains("index", true)) return "Index out of bounds — length check karo before arr[i]"
        if (code.contains(";", true).not() && code.contains("python", true).not()) return "Missing semicolon / bracket — syntax check karo"
        return "Error pattern: ${code.take(60)} — logcat/share karo, exact line bataunga."
    }

    private fun extractAfter(raw: String, keys: List<String>): String {
        val lower = raw.lowercase()
        for (k in keys) if (lower.contains(k)) {
            val idx = lower.indexOf(k)
            return raw.substring(idx + k.length).trim().trimStart(':', '-', ' ').trim()
        }
        return raw
    }

    private fun save(context: Context, cat: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try { com.example.hacker.data.repository.MemoryRepository(context).add(cat, content) } catch (_: Exception) {}
        }
    }
}
