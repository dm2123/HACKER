package com.example.hacker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Insert
    suspend fun insert(log: ActivityLogEntity)

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun observe(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    suspend fun getAll(): List<ActivityLogEntity>

    @Query("DELETE FROM activity_logs")
    suspend fun clear()
}

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(c: ConversationEntity)

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun observe(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    suspend fun getAll(): List<ConversationEntity>

    @Delete
    suspend fun delete(c: ConversationEntity)
}

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(m: MessageEntity)

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun observe(convId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    suspend fun getAll(convId: String): List<MessageEntity>
}

@Dao
interface MemoryDao {
    @Insert
    suspend fun insert(m: MemoryEntity)

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun observe(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    suspend fun getAll(): List<MemoryEntity>

    @Delete
    suspend fun delete(m: MemoryEntity)

    @Query("DELETE FROM memories")
    suspend fun clear()
}

@Dao
interface AutomationDao {
    @Insert
    suspend fun insert(a: AutomationEntity): Long

    @Query("SELECT * FROM automations")
    fun observe(): Flow<List<AutomationEntity>>

    @Query("SELECT * FROM automations WHERE enabled = 1")
    suspend fun getEnabled(): List<AutomationEntity>

    @Update
    suspend fun update(a: AutomationEntity)

    @Delete
    suspend fun delete(a: AutomationEntity)
}
