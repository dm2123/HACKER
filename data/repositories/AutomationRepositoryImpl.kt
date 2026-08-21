package com.example.hacker.data.repositories

import com.example.hacker.data.database.HackerDatabase
import com.example.hacker.data.database.AutomationEntity
import com.example.hacker.domain.entities.Automation
import com.example.hacker.domain.repositories.AutomationRepository

class AutomationRepositoryImpl(private val database: HackerDatabase) : AutomationRepository {
    override fun getAutomations(): List<Automation> {
        // TODO: Implement Room query
        return emptyList()
    }

    override fun getAutomation(id: String): Automation? {
        // TODO: Implement Room query
        return null
    }

    override fun saveAutomation(automation: Automation) {
        // TODO: Implement Room insert
    }

    override fun updateAutomationEnabled(id: String, enabled: Boolean) {
        // TODO: Implement Room update
    }

    override fun deleteAutomation(id: String) {
        // TODO: Implement Room delete
    }
}