package com.example.hacker.data.repository

import android.content.Context
import com.example.hacker.core.logging.ActivityLogger
import com.example.hacker.data.local.ActivityLogEntity
import com.example.hacker.data.local.AutomationEntity
import com.example.hacker.data.local.HackerDatabase
import com.example.hacker.data.local.MemoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityLogRepository(context: Context) : ActivityLogger {
    private val dao = HackerDatabase.getDatabase(context).activityLogDao()

    override fun log(command: String, action: String, result: String, confirmationRequired: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(
                ActivityLogEntity(
                    command = command,
                    action = action,
                    result = result,
                    confirmationRequired = confirmationRequired
                )
            )
        }
    }

    fun observe(): Flow<List<ActivityLogEntity>> = dao.observe()

    suspend fun getAll(): List<ActivityLogEntity> = withContext(Dispatchers.IO) { dao.getAll() }

    suspend fun clear() = withContext(Dispatchers.IO) { dao.clear() }
}

class MemoryRepository(context: Context) {
    private val dao = HackerDatabase.getDatabase(context).memoryDao()

    fun observe(): Flow<List<MemoryEntity>> = dao.observe()

    suspend fun getAll(): List<MemoryEntity> = withContext(Dispatchers.IO) { dao.getAll() }

    suspend fun add(category: String, content: String, importance: Int = 1) =
        withContext(Dispatchers.IO) { dao.insert(MemoryEntity(category = category, content = content, importance = importance)) }

    suspend fun delete(m: MemoryEntity) = withContext(Dispatchers.IO) { dao.delete(m) }

    suspend fun clear() = withContext(Dispatchers.IO) { dao.clear() }
}

class AutomationRepository(context: Context) {
    private val dao = HackerDatabase.getDatabase(context).automationDao()

    fun observe(): Flow<List<AutomationEntity>> = dao.observe()

    suspend fun getEnabled(): List<AutomationEntity> = withContext(Dispatchers.IO) { dao.getEnabled() }

    suspend fun add(name: String, trigger: String, actions: String): Long =
        withContext(Dispatchers.IO) { dao.insert(AutomationEntity(name = name, trigger = trigger, actions = actions)) }

    suspend fun update(a: AutomationEntity) = withContext(Dispatchers.IO) { dao.update(a) }

    suspend fun delete(a: AutomationEntity) = withContext(Dispatchers.IO) { dao.delete(a) }
}
