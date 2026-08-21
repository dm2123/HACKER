package com.example.hacker.domain.repositories

import com.example.hacker.domain.entities.Memory

abstract class MemoryRepository {
    abstract fun getMemories(): List<Memory>
    abstract fun getMemory(id: String): Memory?
    abstract fun saveMemory(memory: Memory)
    abstract fun deleteMemory(id: String)
    abstract fun clearAllMemories()
}