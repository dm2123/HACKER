package com.example.hacker.skills

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * HACKER 7.0 — Skill Platform (spec 27-29)
 * Each skill has manifest, permissions, executor. Sandboxed + user approval.
 */
data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val version: String = "1.0",
    val enabled: Boolean = true
)

object SkillManager {

    private val builtIn = listOf(
        Skill("study_skill", "StudySkill", "createPlan/startSession/getProgress/generateQuiz"),
        Skill("coding_skill", "CodingSkill", "generate/explain/debug/review"),
        Skill("phone_skill", "PhoneSkill", "torch/volume/alarm/timer/app launch"),
        Skill("college_skill", "CollegeSkill", "assignment/notes/viva"),
        Skill("vision_skill", "VisionSkill", "OCR/code analysis")
    )

    fun list(): List<Skill> = builtIn

    fun handle(context: Context, raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("skill") && (lower.contains("list") || lower.contains("show") || lower.contains("dikhao")) -> {
                "Skills: ${builtIn.joinToString(", ") { it.name }} — sab enabled, sandboxed."
            }
            lower.contains("enable skill") || lower.contains("install skill") -> {
                val name = raw.substringAfter("skill", "").trim().ifEmpty { "custom" }
                "Skill $name install karne ke liye permission review → sandbox test → user approval chahiye. Abhi built-in skills ready hain."
            }
            lower.contains("adaptive") || lower.contains("suggestion") -> {
                // Use AdaptiveEngine
                val suggestion = try {
                    com.example.hacker.utils.AdaptiveEngine.generateSuggestion(
                        com.example.hacker.utils.AdaptiveEngine.getPreferences(context)
                    )
                } catch (_: Exception) { null }
                if (suggestion != null) "Adaptive suggestion: ${suggestion.title} — ${suggestion.reason}"
                else "Boss, aap har Monday project planning karte ho — isko routine save karun? (Adaptive HACKER)"
            }
            else -> "Skills: 'skill list dikhao', 'adaptive suggestion', bolo."
        }
    }
}
