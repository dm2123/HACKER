package com.example.hacker.data.repositories

import com.example.hacker.data.database.HackerDatabase
import com.example.hacker.data.database.MemoryEntity
import com.example.hacker.domain.entities.Memory
import com.example.hacker.domain.repositories.MemoryRepository

class MemoryRepositoryImpl(private val database: HackerDatabase) : MemoryRepository {
    override fun getMemories(): List<Memory> {
        // TODO: Implement Room query
        return emptyList()
    }

    override fun getMemory(id: String): Memory? {
        // TODO: Implement Room query
        return null
    }

    override fun saveMemory(memory: Memory) {
        // TODO: Implement Room insert
    }

    override fun deleteMemory(id: String) {
        // TODO: Implement Room delete
    }

    override fun clearAllMemories() {
        // TODO: Implement Room clear
    }
}