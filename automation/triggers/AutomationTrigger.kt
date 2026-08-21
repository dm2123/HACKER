package com.example.hacker.automation.triggers

import com.example.hacker.domain.entities.Automation

/** Triggers that can start automations */
abstract class AutomationTrigger {
    /** Check if trigger condition is met */
    abstract fun isTriggerMet(): Boolean

    /** Get associated automation */
    abstract fun getAssociatedAutomation(): Automation?
}

/** Time-based trigger */
class TimeTrigger(private val time: String) : AutomationTrigger() {
    override fun isTriggerMet(): Boolean {
        // TODO: Check current time against trigger time
        return false
    }

    override fun getAssociatedAutomation(): Automation? {
        // TODO: Return associated automation
        return null
    }
}

/** Voice command trigger */
class VoiceCommandTrigger(private val command: String) : AutomationTrigger() {
    override fun isTriggerMet(): Boolean {
        // TODO: Check if voice command matches
        return false
    }

    override fun getAssociatedAutomation(): Automation? {
        // TODO: Return associated automation
        return null
    }
}

/** Sensor-based trigger */
class SensorTrigger(private val sensorType: String) : AutomationTrigger() {
    override fun isTriggerMet(): Boolean {
        // TODO: Check sensor state
        return false
    }

    override fun getAssociatedAutomation(): Automation? {
        // TODO: Return associated automation
        return null
    }
}