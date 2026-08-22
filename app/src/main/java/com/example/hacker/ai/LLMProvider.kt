package com.example.hacker.ai

import android.content.Context
import com.example.hacker.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * LLM Provider for truly unlimited commands (Option 1).
 * Supports Gemini (free) and OpenAI (paid).
 * User provides API key in Settings → AI Settings.
 */
object LLMProvider {

    data class LLMResponse(
        val action: String,      // torch_on, open_whatsapp, web_search, etc.
        val params: Map<String, String> = emptyMap(),
        val reply: String        // natural language reply
    )

    suspend fun analyze(context: Context, userCommand: String): LLMResponse? {
        val endpoint = UserPreferences.llmEndpoint(context).first()
        val key = UserPreferences.llmApiKey(context).first()
        val model = UserPreferences.llmModel(context).first()

        if (key.isBlank()) return null // No API key configured

        return when {
            endpoint.contains("generativelanguage.googleapis.com") || model.contains("gemini") -> 
                analyzeGemini(key, model.ifBlank { "gemini-pro" }, userCommand)
            endpoint.contains("openai") || model.contains("gpt") -> 
                analyzeOpenAI(key, model.ifBlank { "gpt-3.5-turbo" }, userCommand)
            else -> null
        }
    }

    private fun analyzeGemini(apiKey: String, model: String, command: String): LLMResponse? {
        return try {
            val url = URL("https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val prompt = buildPrompt(command)
            val body = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                })
            }

            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            val response = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            val text = json.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            parseAIResponse(text)
        } catch (e: Exception) {
            null
        }
    }

    private fun analyzeOpenAI(apiKey: String, model: String, command: String): LLMResponse? {
        return try {
            val url = URL("https://api.openai.com/v1/chat/completions")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.doOutput = true

            val prompt = buildPrompt(command)
            val body = JSONObject().apply {
                put("model", model)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("temperature", 0.3)
            }

            conn.outputStream.use { it.write(body.toString().toByteArray()) }

            val response = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            val text = json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            parseAIResponse(text)
        } catch (e: Exception) {
            null
        }
    }

    private fun buildPrompt(command: String): String {
        return """
You are HACKER, a voice assistant for Android. User said: "$command"

Your job: analyze the command and return a JSON response with:
{
  "action": "<action_id>",
  "params": {"key": "value"},
  "reply": "<natural hindi/hinglish reply>"
}

Available actions:
- torch_on, torch_off
- open_whatsapp, open_instagram, open_spotify, open_gmail, open_chrome, open_camera
- play_music (params: song)
- web_search (params: query)
- call_contact (params: name)
- send_message (params: contact, message)
- set_alarm (params: time)
- set_timer (params: minutes)
- volume_up, volume_down
- wifi_settings, bluetooth_settings, battery_settings
- assignment_help, code_help (params: topic)

Examples:
User: "whatsapp par mom ko message bhejo ki main late aaunga"
{"action": "send_message", "params": {"contact": "mom", "message": "main late aaunga"}, "reply": "Yes Boss, mom ko message bhej raha hoon"}

User: "5 minute baad yaad dilao"
{"action": "set_timer", "params": {"minutes": "5"}, "reply": "Yes Boss, 5 minute ka timer laga diya"}

User: "gaana bajao"
{"action": "play_music", "params": {}, "reply": "Yes Boss, music player khol raha hoon"}

Return ONLY valid JSON, no explanation.
        """.trimIndent()
    }

    private fun parseAIResponse(text: String): LLMResponse? {
        return try {
            // Extract JSON from markdown if present
            val jsonText = if (text.contains("```json")) {
                text.substringAfter("```json").substringBefore("```").trim()
            } else if (text.contains("```")) {
                text.substringAfter("```").substringBefore("```").trim()
            } else {
                text.trim()
            }

            val json = JSONObject(jsonText)
            val action = json.getString("action")
            val reply = json.getString("reply")
            val params = mutableMapOf<String, String>()
            
            if (json.has("params")) {
                val paramsObj = json.getJSONObject("params")
                paramsObj.keys().forEach { key ->
                    params[key] = paramsObj.getString(key)
                }
            }

            LLMResponse(action, params, reply)
        } catch (e: Exception) {
            null
        }
    }
}
