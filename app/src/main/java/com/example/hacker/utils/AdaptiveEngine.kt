package com.example.hacker.utils

/**
 * HACKER 7.0 - A detected usage pattern
 */
data class Pattern(
    val patternId: String,
    val trigger: String,
    val action: String,
    val frequency: Int,
    val confidence: Float,
    val lastSeen: Long
)

/**
 * HACKER 7.0 - Learned user preference model
 */
data class PreferenceModel(
    val preferredVoice: String = "Hinglish",
    val preferredLanguage: String = "en-IN",
    val typicalUsageHours: List<Int> = emptyList(),
    val autoApprovedActions: List<String> = emptyList(),
    val disabledFeatures: List<String> = emptyList()
)

/**
 * Adaptive Intelligence Engine - learns from user patterns.
 * Pure Kotlin, no ML dependencies. Persistence wired in a future sprint.
 */
object AdaptiveEngine {

    @Volatile
    private var userPreferences: PreferenceModel = PreferenceModel()

    fun detectPatterns(actionHistory: List<String>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        val seen = mutableSetOf<String>()
        val recentActions = actionHistory.takeLast(100)

        for (action in recentActions) {
            if (seen.contains(action)) {
                continue
            }
            seen.add(action)
            val frequency = recentActions.count { item -> item == action }
            if (frequency >= 3) {
                val confidence = if (frequency > 100) 1.0f else frequency / 100.0f
                patterns.add(
                    Pattern(
                        patternId = action,
                        trigger = action,
                        action = action,
                        frequency = frequency,
                        confidence = confidence,
                        lastSeen = System.currentTimeMillis()
                    )
                )
            }
        }
        return patterns
    }

    fun generateSuggestion(context: String): String? {
        val patterns = detectPatterns(getRecentActions(context))
        if (patterns.isEmpty()) {
            return null
        }

        val top = patterns.maxByOrNull { it.confidence } ?: return null
        if (top.confidence < 0.5f) {
            return null
        }

        if (userPreferences.autoApprovedActions.contains(top.trigger)) {
            return "Suggested: " + top.action + " (auto-approved)"
        }
        return "Suggestion: " + top.action + " (would you like to enable?)"
    }

    fun updatePreferences(action: String, approved: Boolean) {
        // Hook for DataStore persistence - no-op for now
    }

    fun getPreferences(): PreferenceModel = userPreferences

    private fun getRecentActions(context: String): List<String> {
        // Wired to CommandProcessor history in a future sprint
        return emptyList()
    }
}
