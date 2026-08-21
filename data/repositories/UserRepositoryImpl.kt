package com.example.hacker.data.repositories

import com.example.hacker.data.database.HackerDatabase
import com.example.hacker.data.database.UserEntity
import com.example.hacker.domain.entities.User
import com.example.hacker.domain.repositories.UserRepository

class UserRepositoryImpl(private val database: HackerDatabase) : UserRepository {
    override fun getUser(): User {
        // TODO: Implement Room query
        return User(id = "", name = null, language = "en", voiceProfileEnabled = false, memoryEnabled = false, createdAt = 0)
    }

    override fun saveUser(user: User) {
        // TODO: Implement Room insert
    }

    override fun updateUserLanguage(language: String) {
        // TODO: Implement Room update
    }
}