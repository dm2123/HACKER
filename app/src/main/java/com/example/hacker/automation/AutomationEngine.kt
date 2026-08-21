package com.example.hacker.automation

import android.content.Context
import com.example.hacker.phonecontrol.PhoneControl

data class Workflow(val name: String, val actions: List<Pair<String, String>>)

/**
 * Automation Engine (spec section 12).
 * Runs a named workflow by executing its list of tool actions via PhoneControl.
 */
object AutomationEngine {

    private val workflows = mapOf(
        "study mode" to Workflow(
            "Study Mode",
            listOf("volume_down" to "", "wifi" to "", "open_app" to "youtube")
        ),
        "sleep mode" to Workflow(
            "Sleep Mode",
            listOf("volume_down" to "", "bluetooth" to "")
        ),
        "morning routine" to Workflow(
            "Morning Routine",
            listOf("wifi" to "", "open_app" to "whatsapp")
        ),
        "work mode" to Workflow(
            "Work Mode",
            listOf("wifi" to "", "open_app" to "gmail")
        )
    )

    fun run(context: Context, trigger: String): String {
        val wf = workflows[trigger.lowercase()]
            ?: return "Workflow '$trigger' नहीं मिला। उपलब्ध: ${list().joinToString(", ")}"
        wf.actions.forEach { (action, param) -> PhoneControl.handle(context, action, param) }
        return "${wf.name} एक्टिवेट हुआ (${wf.actions.size} एक्शन्स)।"
    }

    fun list(): List<String> = workflows.keys.toList()
}
