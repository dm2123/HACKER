package com.example.hacker.core.ai

import com.example.hacker.domain.entities.IntentEntity

/** Intent parser that converts natural language to structured intents.
 *  Supports Hindi, English and Hinglish without external NLP dependencies. */
class IntentParser {

    /** Parse a natural language command into an IntentEntity */
    fun parse(command: String): IntentEntity {
        val raw = command.trim()
        if (raw.isEmpty()) {
            return IntentEntity(intent = "UNKNOWN", entities = mapOf("raw" to raw), confidence = 0.0f)
        }
        val lower = raw.lowercase()
        val lang = detectLanguage(raw)

        // Helpers for entity extraction
        fun extractAfter(keywords: List<String>): String {
            var out = raw
            for (k in keywords) {
                if (lower.contains(k)) {
                    // find keyword position and take substring after it in original raw
                    val idx = lower.indexOf(k)
                    if (idx >= 0) {
                        out = raw.substring(idx + k.length).trim()
                        break
                    }
                }
            }
            return out.trim().trimStart(':', '-', ' ').trim()
        }

        return when {
            lower.contains("torch") || lower.contains("flashlight") || lower.contains("टॉर्च") || lower.contains("फ्लैश") || lower.contains("लाइट") -> {
                val isOff = lower.contains("off") || lower.contains("बंद") || lower.contains("बुझा")
                IntentEntity(
                    intent = if (isOff) "TORCH_OFF" else "TORCH_ON",
                    entities = mapOf("raw" to raw, "language" to lang),
                    confidence = 0.96f
                )
            }
            lower.contains("volume") || lower.contains("वॉल्यूम") || lower.contains("आवाज़") || lower.contains("आवाज") -> {
                when {
                    lower.contains("up") || lower.contains("बढ़ा") || lower.contains("बढा") || lower.contains("तेज") -> IntentEntity("VOLUME_UP", mapOf("raw" to raw), 0.95f)
                    lower.contains("down") || lower.contains("घटा") || lower.contains("कम") -> IntentEntity("VOLUME_DOWN", mapOf("raw" to raw), 0.95f)
                    else -> {
                        val level = Regex("(\\d{1,3})\\s*%?").find(lower)?.groupValues?.get(1)?.toIntOrNull()
                        IntentEntity("VOLUME_SET", mapOf("raw" to raw, "level" to (level ?: 50)), 0.9f)
                    }
                }
            }
            lower.contains("alarm") || lower.contains("अलार्म") -> {
                val hour = Regex("(\\d{1,2})\\s*(am|pm|बजे|बज)?").find(lower)?.groupValues?.get(1)?.toIntOrNull() ?: 6
                IntentEntity("ALARM_SET", mapOf("raw" to raw, "hour" to hour, "minute" to 0), 0.92f)
            }
            lower.contains("timer") || lower.contains("टाइमर") -> {
                val mins = Regex("(\\d+)\\s*(minute|min|मिनट)").find(lower)?.groupValues?.get(1)?.toIntOrNull() ?: 1
                IntentEntity("TIMER_START", mapOf("raw" to raw, "minutes" to mins, "seconds" to mins * 60), 0.93f)
            }
            lower.startsWith("call ") || lower.contains(" कॉल") || lower.contains("फोन कर") || lower.contains("call ") -> {
                val target = extractAfter(listOf("call ", "कॉल ", "फोन कर "))
                IntentEntity("CALL", mapOf("raw" to raw, "target" to target.ifEmpty { raw }), 0.94f)
            }
            lower.contains("sms") || lower.contains("message") || lower.contains("संदेश") || lower.contains("मैसेज") -> {
                IntentEntity("SMS_OPEN", mapOf("raw" to raw), 0.9f)
            }
            lower.startsWith("open ") || lower.contains("खोलो") || lower.contains("ओपन") || lower.contains("open ") -> {
                val app = extractAfter(listOf("open ", "खोलो", "ओपन"))
                if (app.isEmpty()) IntentEntity("UNKNOWN", mapOf("raw" to raw), 0.4f)
                else IntentEntity("OPEN_APP", mapOf("raw" to raw, "appName" to app), 0.94f)
            }
            lower.contains("wifi") || lower.contains("वाईफाई") || lower.contains("wi-fi") -> IntentEntity("WIFI_TOGGLE", mapOf("raw" to raw), 0.9f)
            lower.contains("bluetooth") || lower.contains("ब्लूटूथ") -> IntentEntity("BLUETOOTH_TOGGLE", mapOf("raw" to raw), 0.9f)
            lower.contains("youtube") || lower.contains("यूट्यूब") -> {
                val q = extractAfter(listOf("youtube", "यूट्यूब"))
                IntentEntity("YOUTUBE_SEARCH", mapOf("raw" to raw, "query" to q), 0.91f)
            }
            lower.startsWith("search ") || lower.contains("खोजो") || lower.startsWith("google ") || lower.contains("गूगल") -> {
                val q = extractAfter(listOf("search ", "खोजो", "google ", "गूगल"))
                IntentEntity("WEB_SEARCH", mapOf("raw" to raw, "query" to q.ifEmpty { raw }), 0.92f)
            }
            lower.startsWith("play ") || lower.contains("चलाओ") || lower.contains("बजाओ") || lower.contains("गाना") || lower.contains("song") -> {
                var q = extractAfter(listOf("play ", "चलाओ", "बजाओ"))
                q = q.replace("गाना", "", true).replace("song", "", true).trim()
                IntentEntity("PLAY_MUSIC", mapOf("raw" to raw, "query" to q.ifEmpty { "trending songs" }), 0.9f)
            }
            lower.contains("weather") || lower.contains("मौसम") || lower.contains("mausam") -> {
                val city = raw.replace(Regex("(?i)weather|मौसम|mausam|कैसा है"), "").trim().ifEmpty { "delhi" }
                IntentEntity("WEATHER_QUERY", mapOf("raw" to raw, "city" to city), 0.9f)
            }
            lower.contains("time") || lower.contains("समय") || lower.contains("टाइम") -> IntentEntity("TIME_QUERY", mapOf("raw" to raw), 0.95f)
            lower.contains("date") || lower.contains("तारीख") || lower.contains("आज") -> IntentEntity("DATE_QUERY", mapOf("raw" to raw), 0.95f)
            lower.contains("battery") || lower.contains("बैटरी") || lower.contains("चार्ज") -> IntentEntity("BATTERY_QUERY", mapOf("raw" to raw), 0.95f)
            lower.contains("map") || lower.contains("नक्शा") || lower.contains("लोकेशन") -> {
                val q = extractAfter(listOf("map ", "नक्शा", "लोकेशन"))
                IntentEntity("MAP_SEARCH", mapOf("raw" to raw, "query" to q), 0.88f)
            }
            lower.contains("remember") || lower.contains("याद रख") || lower.contains("yaad rakh") -> {
                val what = extractAfter(listOf("remember", "याद रख", "yaad rakh"))
                IntentEntity("MEMORY_SAVE", mapOf("raw" to raw, "content" to what.ifEmpty { raw }), 0.9f)
            }
            lower.contains("study mode") || lower.contains("स्टडी मोड") -> IntentEntity("STUDY_MODE", mapOf("raw" to raw), 0.9f)
            lower.contains("sleep mode") || lower.contains("स्लीप मोड") -> IntentEntity("SLEEP_MODE", mapOf("raw" to raw), 0.9f)
            lower.contains("hello") || lower.contains("hi ") || lower.contains("नमस्ते") || lower.contains("hey") -> IntentEntity("GREETING", mapOf("raw" to raw), 0.95f)
            // Math
            Regex("\\d+\\s*[+\\-*/x]\\s*\\d+").containsMatchIn(lower) || lower.contains("plus") || lower.contains("minus") || lower.contains("into ") -> {
                IntentEntity("MATH_EVAL", mapOf("raw" to raw), 0.85f)
            }
            else -> IntentEntity(intent = "UNKNOWN", entities = mapOf("raw" to raw, "language" to lang), confidence = 0.4f)
        }
    }

    /** Detect language of the input: hi / hinglish / en */
    fun detectLanguage(text: String): String {
        if (text.isBlank()) return "en"
        // Devanagari block U+0900..U+097F
        val hasDevanagari = text.any { it.code in 0x0900..0x097F }
        if (hasDevanagari) {
            // if also has latin, treat as hinglish
            val hasLatin = text.any { it in 'a'..'z' || it in 'A'..'Z' }
            return if (hasLatin) "hinglish" else "hi"
        }
        val lower = text.lowercase()
        val hinglishMarkers = listOf("karo", "kholo", "batao", "chalao", "bajao", "kar", "hai", "hain", "kya", "kaise", "chal", "bhejo", "lagao", "chahiye", "wala", "mera", "tera")
        if (hinglishMarkers.any { lower.contains(it) }) return "hinglish"
        return "en"
    }
}
