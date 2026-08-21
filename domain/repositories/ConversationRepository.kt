package com.example.hacker.domain.repositories

import com.example.hacker.domain.entities.Conversation
import com.example.hacker.domain.entities.Message

abstract class ConversationRepository {
    abstract fun getConversations(): List<Conversation>
    abstract fun getConversation(id: String): Conversation?
    abstract fun saveConversation(conversation: Conversation)
    abstract fun deleteConversation(id: String)
    abstract fun getMessages(conversationId: String): List<Message>
    abstract fun saveMessage(message: Message)
    abstract fun deleteAllMessages()
}