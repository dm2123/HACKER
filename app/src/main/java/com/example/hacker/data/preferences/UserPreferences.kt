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
    private val LLM_API_KEY = stringPreferencesKey("llm_api_key")
    private val LLM_ENDPOINT = stringPreferencesKey("llm_endpoint")
    private val LLM_MODEL = stringPreferencesKey("llm_model")
    private val SECURITY_PIN_HASH = stringPreferencesKey("security_pin_hash")
    private val SECURITY_ENABLED = booleanPreferencesKey("security_enabled")
    private val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    private val CLOUD_AI_ENABLED = booleanPreferencesKey("cloud_ai_enabled")
    private val CONTACTS_ENABLED = booleanPreferencesKey("contacts_enabled")
    private val LOCATION_ENABLED = booleanPreferencesKey("location_enabled")
    private val ACCESSIBILITY_ENABLED = booleanPreferencesKey("accessibility_enabled")
    private val APPEARANCE_DARK = booleanPreferencesKey("appearance_dark")
    private val VOICE_PITCH = stringPreferencesKey("voice_pitch")
    private val VOICE_SPEED = stringPreferencesKey("voice_speed")

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

    fun llmApiKey(context: Context): Flow<String> =
        context.dataStore.data.map { it[LLM_API_KEY] ?: "" }

    suspend fun setLlmApiKey(context: Context, value: String) {
        context.dataStore.edit { it[LLM_API_KEY] = value }
    }

    fun llmEndpoint(context: Context): Flow<String> =
        context.dataStore.data.map { it[LLM_ENDPOINT] ?: "https://api.openai.com/v1/chat/completions" }

    suspend fun setLlmEndpoint(context: Context, value: String) {
        context.dataStore.edit { it[LLM_ENDPOINT] = value }
    }

    fun llmModel(context: Context): Flow<String> =
        context.dataStore.data.map { it[LLM_MODEL] ?: "gpt-4o-mini" }

    suspend fun setLlmModel(context: Context, value: String) {
        context.dataStore.edit { it[LLM_MODEL] = value }
    }

    fun securityPinHash(context: Context): Flow<String> =
        context.dataStore.data.map { it[SECURITY_PIN_HASH] ?: "" }

    suspend fun setSecurityPinHash(context: Context, value: String) {
        context.dataStore.edit { it[SECURITY_PIN_HASH] = value }
    }

    fun securityEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[SECURITY_ENABLED] ?: false }

    suspend fun setSecurityEnabled(context: Context, v: Boolean) {
        context.dataStore.edit { it[SECURITY_ENABLED] = v }
    }

    fun onboardingDone(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[ONBOARDING_DONE] ?: false }
    suspend fun setOnboardingDone(context: Context, v: Boolean) { context.dataStore.edit { it[ONBOARDING_DONE] = v } }

    fun notificationsEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: false }
    suspend fun setNotificationsEnabled(context: Context, v: Boolean) { context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = v } }

    fun cloudAiEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[CLOUD_AI_ENABLED] ?: false }
    suspend fun setCloudAiEnabled(context: Context, v: Boolean) { context.dataStore.edit { it[CLOUD_AI_ENABLED] = v } }

    fun contactsEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[CONTACTS_ENABLED] ?: true }
    suspend fun setContactsEnabled(context: Context, v: Boolean) { context.dataStore.edit { it[CONTACTS_ENABLED] = v } }

    fun locationEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[LOCATION_ENABLED] ?: false }
    suspend fun setLocationEnabled(context: Context, v: Boolean) { context.dataStore.edit { it[LOCATION_ENABLED] = v } }

    fun accessibilityEnabled(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[ACCESSIBILITY_ENABLED] ?: false }
    suspend fun setAccessibilityEnabled(context: Context, v: Boolean) { context.dataStore.edit { it[ACCESSIBILITY_ENABLED] = v } }

    fun appearanceDark(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[APPEARANCE_DARK] ?: true }
    suspend fun setAppearanceDark(context: Context, v: Boolean) { context.dataStore.edit { it[APPEARANCE_DARK] = v } }

    fun voicePitch(context: Context): Flow<String> =
        context.dataStore.data.map { it[VOICE_PITCH] ?: "1.0" }
    suspend fun setVoicePitch(context: Context, v: String) { context.dataStore.edit { it[VOICE_PITCH] = v } }

    fun voiceSpeed(context: Context): Flow<String> =
        context.dataStore.data.map { it[VOICE_SPEED] ?: "1.0" }
    suspend fun setVoiceSpeed(context: Context, v: String) { context.dataStore.edit { it[VOICE_SPEED] = v } }
}
