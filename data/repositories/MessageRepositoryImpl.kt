package com.example.hacker.data.repositories

import com.example.hacker.data.database.HackerDatabase
import com.example.hacker.data.database.MessageEntity
import com.example.hacker.domain.entities.Message
import com.example.hacker.domain.repositories.MessageRepository

class MessageRepositoryImpl(private val database: HackerDatabase) : MessageRepository {
    override fun getMessages(conversationId: String): List<Message> {
        // TODO: Implement Room query
        return emptyList()
    }

    override fun saveMessage(message: Message) {
        // TODO: Implement Room insert
    }

    override fun deleteAllMessages() {
        // TODO: Implement Room delete
    }
}