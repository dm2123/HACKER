package com.example.hacker.ai

import android.content.Context
import com.example.hacker.data.preferences.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * LLM integration (spec section 5).
 * Talks to any OpenAI-compatible chat completions endpoint.
 * API key + endpoint + model are user-configurable in Settings (stored in DataStore).
 * Falls back gracefully when no key is configured.
 */
object LlmClient {

    data class Config(val endpoint: String, val apiKey: String, val model: String)

    suspend fun loadConfig(context: Context): Config {
        val endpoint = UserPreferences.llmEndpoint(context).first()
            .ifBlank { "https://api.openai.com/v1/chat/completions" }
        val key = UserPreferences.llmApiKey(context).first()
        val model = UserPreferences.llmModel(context).first().ifBlank { "gpt-4o-mini" }
        return Config(endpoint, key, model)
    }

    suspend fun isConfigured(context: Context): Boolean =
        loadConfig(context).apiKey.isNotBlank()

    /**
     * Sends the conversation to the LLM and returns its reply, or null on failure.
     */
    suspend fun chat(context: Context, userMessage: String, history: List<Pair<String, String>> = emptyList()): String? {
        val cfg = loadConfig(context)
        if (cfg.apiKey.isBlank()) return null

        return withContext(Dispatchers.IO) {
            try {
                val arr = JSONArray()
                arr.put(
                    JSONObject().put(
                        "role", "system"
                    ).put(
                        "content",
                        "You are HACKER, a personal voice assistant on the user's Android phone. " +
                            "Reply briefly in Hinglish (Hindi in Latin or Devanagari script mixed with English) " +
                            "unless asked otherwise. You can suggest commands like: time, battery, torch, " +
                            "call <name>, open <app>, wifi, bluetooth, alarm <hour>, timer <minutes>, " +
                            "youtube <query>, search <query>, study mode."
                    )
                )
                history.takeLast(8).forEach { (role, content) ->
                    arr.put(JSONObject().put("role", role).put("content", content))
                }
                arr.put(JSONObject().put("role", "user").put("content", userMessage))

                val body = JSONObject()
                    .put("model", cfg.model)
                    .put("messages", arr)
                    .put("max_tokens", 300)
                    .put("temperature", 0.7)

                val conn = URL(cfg.endpoint).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 15000
                conn.readTimeout = 30000
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Authorization", "Bearer ${cfg.apiKey}")

                conn.outputStream.use { it.write(body.toString().toByteArray()) }

                val code = conn.responseCode
                val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                val text = stream?.let { s ->
                    BufferedReader(InputStreamReader(s)).use { r -> r.readText() }
                } ?: ""
                conn.disconnect()

                if (code in 200..299) {
                    val json = JSONObject(text)
                    json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()
                } else null
            } catch (_: Exception) {
                null
            }
        }
    }
}
