package com.example.hacker.utils

/**
 * HACKER 5.0 - Goal Intent classification
 */
enum class GoalIntent {
    STUDY_PLAN,
    ASSIGNMENT_WORKFLOW,
    CODING_SESSION,
    EXAM_PREP,
    PERSONAL_PROJECT,
    UNKNOWN
}

/**
 * A parsed user goal
 */
data class Goal(
    val rawText: String,
    val intent: GoalIntent,
    val confidence: Float = 1.0f,
    val estimatedDuration: Long = 3600000L,
    val deadline: Long? = null
)

/**
 * HACKER 5.0 - Natural language to goal parser.
 * Pure Kotlin, no external dependencies.
 */
object GoalParser {

    fun parse(text: String): Goal {
        val lower = text.lowercase().trim()

        val intent = when {
            lower.contains("study") && lower.contains("plan") -> GoalIntent.STUDY_PLAN
            lower.contains("assignment") || lower.contains("homework") -> GoalIntent.ASSIGNMENT_WORKFLOW
            lower.contains("code") || lower.contains("program") -> GoalIntent.CODING_SESSION
            lower.contains("exam") || lower.contains("test prep") -> GoalIntent.EXAM_PREP
            lower.contains("project") -> GoalIntent.PERSONAL_PROJECT
            else -> GoalIntent.UNKNOWN
        }

        val confidence = if (intent == GoalIntent.UNKNOWN) 0.4f else 0.85f

        return Goal(
            rawText = text,
            intent = intent,
            confidence = confidence
        )
    }
}
