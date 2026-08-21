package com.example.hacker.data.repositories

import com.example.hacker.data.database.HackerDatabase
import com.example.hacker.data.database.ConversationEntity
import com.example.hacker.domain.entities.Conversation
import com.example.hacker.domain.repositories.ConversationRepository

class ConversationRepositoryImpl(private val database: HackerDatabase) : ConversationRepository {
    override fun getConversations(): List<Conversation> {
        // TODO: Implement Room query
        return emptyList()
    }

    override fun getConversation(id: String): Conversation? {
        // TODO: Implement Room query
        return null
    }

    override fun saveConversation(conversation: Conversation) {
        // TODO: Implement Room insert
    }

    override fun deleteConversation(id: String) {
        // TODO: Implement Room delete
    }
}