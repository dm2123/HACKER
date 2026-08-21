package com.example.hacker.automation.workflows

import com.example.hacker.domain.entities.Automation
import com.example.hacker.automation.engine.AutomationEngine
import com.example.hacker.automation.triggers.AutomationTrigger

/** User-defined automation workflow */
data class UserWorkflow(
    val id: String,
    val name: String,
    val description: String,
    val trigger: AutomationTrigger,
    val actions: List<String>,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/** Predefined workflow templates */
object WorkflowTemplates {
    /** Study mode workflow */
    val studyMode = UserWorkflow(
        id = "study_mode_001",
        name = "Study Mode",
        description = "Focus mode: DND, brightness, timer",
        trigger = VoiceCommandTrigger("Study Mode on karo"),
        actions = listOf(
            "DoNotDisturb enable",
            "Brightness 30%",
            "Open Study App",
            "Timer 60 minutes"
        ),
        enabled = true
    )

    /** Sleep mode workflow */
    val sleepMode = UserWorkflow(
        id = "sleep_mode_001",
        name = "Sleep Mode",
        description = "Night mode: DND, torch off, timer",
        trigger = VoiceCommandTrigger("Sleep Mode on karo"),
        actions = listOf(
            "DoNotDisturb enable",
            "Torch off",
            "Set night timer"
        ),
        enabled = true
    )

    /** Morning routine */
    val morningRoutine = UserWorkflow(
        id = "morning_routine_001",
        name = "Morning Routine",
        description = "Wake up sequence: flashlight, news, timer",
        trigger = VoiceCommandTrigger("Morning Routine shuru karo"),
        actions = listOf(
            "Flashlight on",
            "Open News App",
            "Timer 10 minutes"
        ),
        enabled = true
    )
}