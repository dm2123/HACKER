package com.example.hacker.domain.repositories

import com.example.hacker.domain.entities.Automation

abstract class AutomationRepository {
    abstract fun getAutomations(): List<Automation>
    abstract fun getAutomation(id: String): Automation?
    abstract fun saveAutomation(automation: Automation)
    abstract fun updateAutomationEnabled(id: String, enabled: Boolean)
    abstract fun deleteAutomation(id: String)
}