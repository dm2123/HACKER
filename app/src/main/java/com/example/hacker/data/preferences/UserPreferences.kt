package com.example.hacker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "hacker_prefs")

object UserPreferences {
    private val LANGUAGE = stringPreferencesKey("language")
    private val VOICE_PROFILE = booleanPreferencesKey("voice_profile_enabled")
    private val MEMORY_ENABLED = booleanPreferencesKey("memory_enabled")

    fun language(context: Context): Flow<String> =
        context.dataStore.data.map { it[LANGUAGE] ?: "hi-IN" }

    suspend fun setLanguage(context: Context, value: String) {
        context.dataStore.edit { it[LANGUAGE] = value }
    }

    fun voiceProfileEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[VOICE_PROFILE] ?: false }

    suspend fun setVoiceProfileEnabled(context: Context, v: Boolean) {
        context.dataStore.edit { it[VOICE_PROFILE] = v }
    }

    fun memoryEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[MEMORY_ENABLED] ?: true }

    suspend fun setMemoryEnabled(context: Context, v: Boolean) {
        context.dataStore.edit { it[MEMORY_ENABLED] = v }
    }
}
