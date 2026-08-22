package com.example.hacker.utils

/**
 * HACKER 5.0 - A single audited action
 */
data class AuditEntry(
    val timestamp: Long,
    val action: String,
    val details: String
)

/**
 * In-memory audit trail of every action HACKER performs.
 * Wired to Room persistence in a future sprint.
 */
object AuditLogger {

    private val entries = mutableListOf<AuditEntry>()

    @Synchronized
    fun log(action: String, details: String) {
        entries.add(AuditEntry(System.currentTimeMillis(), action, details))
        if (entries.size > 500) {
            entries.removeAt(0)
        }
    }

    @Synchronized
    fun recent(count: Int): List<AuditEntry> {
        if (entries.isEmpty()) {
            return emptyList()
        }
        val from = if (count >= entries.size) 0 else entries.size - count
        return entries.subList(from, entries.size).toList()
    }

    @Synchronized
    fun summary(): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        for (entry in entries) {
            val current = map[entry.action]
            if (current == null) {
                map[entry.action] = 1
            } else {
                map[entry.action] = current + 1
            }
        }
        return map
    }

    @Synchronized
    fun clear() {
        entries.clear()
    }
}
