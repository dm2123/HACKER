package com.example.hacker.data.database

import android.content.Context
import com.example.hacker.data.models.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportMatrix

@Database(entities = [
    User::class,
    Conversation::class,
    Message::class,
    Memory::class,
    Automation::class,
    ActivityLog::class
], version = 1, exportSchema = false)
abstract class HackerDatabase : RoomDatabase() {

    abstract fun userRepository(): UserRepository
    abstract fun conversationRepository(): ConversationRepository
    abstract fun messageRepository(): MessageRepository
    abstract fun memoryRepository(): MemoryRepository
    abstract fun automationRepository(): AutomationRepository
    abstract fun activityLogRepository(): ActivityLogRepository

    companion object {
        @Volatile
        private var INSTANCE: HackerDatabase? = null

        fun getDatabase(context: Context): HackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context.applicationContext,
                    HackerDatabase::class.java,
                    "hacker-db"
                ).build()
                INSTANCE = database
                database
            }
        }

        fun clearAll() {
            INSTANCE = null
        }
    }
}