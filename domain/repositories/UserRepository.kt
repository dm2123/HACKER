package com.example.hacker.domain.repositories

import com.example.hacker.domain.entities.User

abstract class UserRepository {
    abstract fun getUser(): User
    abstract fun saveUser(user: User)
    abstract fun updateUserLanguage(language: String)
}