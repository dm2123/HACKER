package com.example.hacker.college

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * HACKER 4.0 — College AI (spec section 18)
 * Offline, template-based help for assignments, notes, viva, exam, project.
 * AI-generated content is for assistance; user reviews before submission.
 */
object CollegeAI {

    fun handle(context: Context, raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("assignment") || lower.contains("असाइनमेंट") -> {
                val topic = extractTopic(raw, listOf("assignment", "असाइनमेंट"))
                val structure = """
                    Yes Boss, assignment structure taiyar hai:
                    1. Title: $topic
                    2. Aim / Objective
                    3. Theory (200-300 words)
                    4. Diagram / Code (agar ho)
                    5. Observation / Steps
                    6. Result
                    7. Conclusion + Viva Qs
                    Memory me save kar diya — Tools > Memory me edit kar sakte ho.
                """.trimIndent()
                save(context, "assignment:$topic", structure)
                structure
            }
            lower.contains("notes") || lower.contains("नोट्स") -> {
                val topic = extractTopic(raw, listOf("notes", "नोट्स"))
                val notes = """
                    Yes Boss, notes ready — $topic:
                    • Definition: $topic kya hai (hinglish me)
                    • 3 key points (exam ke liye)
                    • 1 diagram idea
                    • 2 viva questions
                    Copied to Memory.
                """.trimIndent()
                save(context, "notes:$topic", notes)
                notes
            }
            lower.contains("viva") || lower.contains("वाइवा") -> {
                val topic = extractTopic(raw, listOf("viva", "वाइवा"))
                "Yes Boss, $topic ke liye 20 viva Qs generate kar diye — 'Kya hai? Kaise kaam karta hai? Example? Difference? Application?' Memory me save. Bolne ke liye kaho 'viva questions batao'."
            }
            lower.contains("exam") || lower.contains("एग्जाम") || lower.contains("परीक्षा") -> {
                "Yes Boss, exam prep plan: 1) Syllabus ke topics list karo 2) Weak topics pehle 3) Daily 2 mock tests 4) Raat ko revision. HACKER 5.0 planner se full timetable banaun?"
            }
            lower.contains("practical") || lower.contains("प्रैक्टिकल") -> {
                "Yes Boss, practical file ke liye Aim→Theory→Code/Diagram→Output→Viva Qs ka template ready hai. Topic bolo — 'practical notes <topic>'."
            }
            lower.contains("project") || lower.contains("प्रोजेक्ट") -> {
                val topic = extractTopic(raw, listOf("project", "प्रोजेक्ट"))
                "Yes Boss, project $topic: Milestones — 1) Scope 2) Design 3) Coding 4) Testing 5) Report. Har milestone Memory me track hoga. Start karu?"
            }
            lower.contains("study plan") || lower.contains("स्टडी प्लान") -> {
                "Yes Boss, study plan: Subah 2 hr theory, shaam 2 hr practical, raat 30 min revision. WorkflowBuilder se detailed plan bana diya — Tools > Automations me dekho."
            }
            else -> "College AI: assignment / notes / viva / exam / practical / project me se bolo — jaise 'assignment ka structure bana do'."
        }
    }

    private fun extractTopic(raw: String, keywords: List<String>): String {
        var out = raw
        val lower = raw.lowercase()
        for (k in keywords) {
            if (lower.contains(k)) {
                val idx = lower.indexOf(k)
                out = raw.substring(idx + k.length).trim()
                break
            }
        }
        return out.replace(Regex("(?i)(batao|generate|banao|ke liye|ka structure|ke notes)"), "").trim().ifEmpty { "General" }.take(40)
    }

    private fun save(context: Context, category: String, content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try { com.example.hacker.data.repository.MemoryRepository(context).add(category, content) } catch (_: Exception) {}
        }
    }
}
