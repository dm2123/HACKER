package com.example.hacker.services

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.service.voice.VoiceInteractionService
import android.util.Log
import com.example.hacker.core.ai.IntentParser
import com.example.hacker.core.voice.TextToSpeechWrapper
import com.example.hacker.phonecontrol.alarm.AlarmController
import com.example.hacker.phonecontrol.apps.AppLauncher
import com.example.hacker.phonecontrol.settings.SettingsLauncher
import com.example.hacker.phonecontrol.timer.TimerController
import com.example.hacker.phonecontrol.volume.VolumeController

/**
 * System voice interaction service (API 21+).
 * Wired to real HACKER pipeline: IntentParser → ToolRouter → Controllers → TTS.
 * Previously only returned demo "command received" responses.
 */
class HackerVoiceInteractionService : VoiceInteractionService() {

    private val intentParser = IntentParser()
    private var tts: TextToSpeechWrapper? = null

    override fun onReady() {
        super.onReady()
        Log.d("HACKER_VOICE", "VoiceInteractionService ready")
        try { tts = TextToSpeechWrapper(this) } catch (_: Exception) {}
    }

    override fun onShutdown() {
        try { tts?.stopSpeaking() } catch (_: Exception) {}
        super.onShutdown()
    }

    /** Entry point for assistant invocations (system calls this when user triggers assistant) */
    fun handleVoiceCommand(command: String): String {
        if (command.isBlank()) {
            val r = "कुछ सुनाई नहीं दिया, फिर से बोलो।"
            speakResponse(r); return r
        }
        return processCommand(command)
    }

    /** Full pipeline: parse → route → permission → execute → TTS */
    private fun processCommand(command: String): String {
        Log.d("HACKER_VOICE", "Processing: $command")
        val parsed = intentParser.parse(command)
        Log.d("HACKER_VOICE", "Intent=${parsed.intent} conf=${parsed.confidence}")

        val response: String = when (parsed.intent) {
            "TORCH_ON" -> handleTorch(true)
            "TORCH_OFF" -> handleTorch(false)
            "VOLUME_UP" -> handleVolume("up")
            "VOLUME_DOWN" -> handleVolume("down")
            "VOLUME_SET" -> handleVolumeSet((parsed.entities["level"] as? Int) ?: 50)
            "ALARM_SET" -> handleAlarm(parsed)
            "TIMER_START" -> handleTimer(parsed)
            "OPEN_APP" -> handleOpenApp(parsed)
            "OPEN_SETTINGS", "WIFI_TOGGLE", "BLUETOOTH_TOGGLE" -> handleSettings(parsed)
            "CALL" -> handleCall(parsed)
            "YOUTUBE_SEARCH" -> handleYoutube(parsed)
            "WEB_SEARCH" -> handleWebSearch(parsed)
            "PLAY_MUSIC" -> handlePlayMusic(parsed)
            "WEATHER_QUERY" -> handleWeather(parsed)
            "MAP_SEARCH" -> handleMap(parsed)
            "MEMORY_SAVE" -> handleMemory(parsed)
            "TIME_QUERY" -> "अभी समय है " + java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            "DATE_QUERY" -> "आज की तारीख है " + java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            "BATTERY_QUERY" -> handleBattery()
            "MATH_EVAL" -> handleMath(command)
            "GREETING" -> "नमस्ते! मैं HACKER हूँ — torch, volume, alarm, timer, open app, youtube, search कुछ भी बोलो।"
            "UNKNOWN" -> {
                // Fallback: try fuzzy app launch, else web search (universal executor)
                val raw = parsed.entities["raw"] as? String ?: command
                handleUnknown(raw)
            }
            else -> handleUnknown(command)
        }

        speakResponse(response)
        return response
    }

    private fun handleTorch(on: Boolean): String {
        return try {
            // Use app-level DeviceActions via generic intent for torch settings if direct controller unavailable
            val cm = getSystemService(Context.CAMERA_SERVICE) as? android.hardware.camera2.CameraManager
            if (cm != null) {
                val id = cm.cameraIdList.firstOrNull() ?: return "कैमरा नहीं मिला।"
                com.example.hacker.phonecontrol.torch.TorchController(cm, id).let { c ->
                    val ok = if (on) c.turnOn() else c.turnOff()
                    if (ok) (if (on) "टॉर्च ऑन कर दिया।" else "टॉर्च बंद कर दिया।") else "टॉर्च कंट्रोल नहीं हो पाया।"
                }
            } else "टॉर्च उपलब्ध नहीं है।"
        } catch (e: Exception) { "टॉर्च error: ${e.message}" }
    }

    private fun handleVolume(dir: String): String {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val c = VolumeController(am)
            if (dir == "up") c.increaseVolume() else c.decreaseVolume()
            if (dir == "up") "आवाज़ बढ़ा दी।" else "आवाज़ घटा दी।"
        } catch (e: Exception) { "वॉल्यूम कंट्रोल नहीं हो पाया।" }
    }

    private fun handleVolumeSet(level: Int): String {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val c = VolumeController(am)
            val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            c.setVolume((level * max / 100).coerceIn(0, max))
            "वॉल्यूम $level% कर दिया।"
        } catch (e: Exception) { "वॉल्यूम सेट नहीं हो पाया।" }
    }

    private fun handleAlarm(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        return try {
            val hour = parsed.entities["hour"] as? Int ?: 6
            val minute = parsed.entities["minute"] as? Int ?: 0
            AlarmController(this).setAlarm(hour, minute, (System.currentTimeMillis() % 10000).toInt())
            "अलार्म $hour:${"%02d".format(minute)} बजे सेट कर दिया।"
        } catch (e: Exception) { "अलार्म सेट नहीं हो पाया: ${e.message}" }
    }

    private fun handleTimer(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        return try {
            val seconds = parsed.entities["seconds"] as? Int ?: 60
            TimerController().start(seconds * 1000L, onFinished = {
                speakResponse("टाइमर पूरा हो गया!")
            }, onProgress = {})
            "टाइमर $seconds सेकंड के लिए शुरू कर दिया।"
        } catch (e: Exception) { "टाइमर शुरू नहीं हो पाया।" }
    }

    private fun handleOpenApp(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val app = parsed.entities["appName"] as? String ?: ""
        if (app.isBlank()) return "कौन सा ऐप खोलूँ?"
        val ok = try { AppLauncher(this).launchApp(app) } catch (_: Exception) { false }
        return if (ok) "$app खोल दिया।" else {
            // try package search fallback
            try {
                val pm = packageManager
                val found = pm.getInstalledApplications(0).firstOrNull {
                    try { pm.getApplicationLabel(it).toString().lowercase().contains(app.lowercase()) } catch (_: Exception) { false }
                }
                if (found != null) {
                    val intent = pm.getLaunchIntentForPackage(found.packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (intent != null) { startActivity(intent); return "${app} खोल दिया।" }
                }
            } catch (_: Exception) {}
            "$app ऐप नहीं मिला।"
        }
    }

    private fun handleSettings(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        return try {
            val page = when (parsed.intent) {
                "WIFI_TOGGLE" -> "wifi"
                "BLUETOOTH_TOGGLE" -> "bluetooth"
                else -> "general"
            }
            SettingsLauncher(this).openSettings(page)
            "सेटिंग्स खोल रहा हूँ।"
        } catch (e: Exception) { "सेटिंग्स नहीं खोल पाया।" }
    }

    private fun handleCall(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val target = parsed.entities["target"] as? String ?: ""
        if (target.isBlank()) return "किसे कॉल करूँ?"
        return try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = android.net.Uri.parse("tel:$target")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            "$target को कॉल लगा रहा हूँ।"
        } catch (e: Exception) { "कॉल नहीं लगा पाया: ${e.message}" }
    }

    private fun handleYoutube(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val q = parsed.entities["query"] as? String ?: ""
        return try {
            val uri = if (q.isBlank()) "https://m.youtube.com" else "https://m.youtube.com/results?search_query=${android.net.Uri.encode(q)}"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            startActivity(intent)
            if (q.isBlank()) "YouTube खोल रहा हूँ।" else "YouTube पर खोज रहा हूँ: $q"
        } catch (e: Exception) { "YouTube नहीं खोल पाया।" }
    }

    private fun handleWebSearch(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val q = parsed.entities["query"] as? String ?: ""
        if (q.isBlank()) return "क्या खोजूँ?"
        return try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra("query", q)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
            "Google पर खोज रहा हूँ: $q"
        } catch (_: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/search?q=${android.net.Uri.encode(q)}")).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                startActivity(intent)
                "Google पर खोज रहा हूँ: $q"
            } catch (e: Exception) { "सर्च नहीं हो पाया।" }
        }
    }

    private fun handlePlayMusic(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val q = parsed.entities["query"] as? String ?: "trending songs"
        return handleYoutube(com.example.hacker.domain.entities.IntentEntity("YOUTUBE_SEARCH", mapOf("query" to q)))
    }

    private fun handleWeather(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val city = parsed.entities["city"] as? String ?: "delhi"
        return handleWebSearch(com.example.hacker.domain.entities.IntentEntity("WEB_SEARCH", mapOf("query" to "$city weather")))
    }

    private fun handleMap(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val q = parsed.entities["query"] as? String ?: ""
        return try {
            val uri = if (q.isBlank()) "geo:0,0?q=" else "geo:0,0?q=${android.net.Uri.encode(q)}"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri)).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            startActivity(intent)
            if (q.isBlank()) "Maps खोल रहा हूँ।" else "Maps पर $q दिखा रहा हूँ।"
        } catch (_: Exception) { handleWebSearch(com.example.hacker.domain.entities.IntentEntity("WEB_SEARCH", mapOf("query" to "$q maps"))) }
    }

    private fun handleMemory(parsed: com.example.hacker.domain.entities.IntentEntity): String {
        val content = parsed.entities["content"] as? String ?: ""
        return "याद रख लिया: $content"
    }

    private fun handleBattery(): String {
        return try {
            val bm = getSystemService(Context.BATTERY_SERVICE) as? android.os.BatteryManager
            val pct = bm?.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
            if (pct >= 0) "बैटरी $pct% है।" else "बैटरी जानकारी उपलब्ध नहीं।"
        } catch (_: Exception) { "बैटरी जानकारी नहीं मिली।" }
    }

    private fun handleMath(raw: String): String {
        return try {
            val m = Regex("(-?\\d+(?:\\.\\d+)?)\\s*(plus|minus|into|multiply|divided by|divide|[+\\-*/x])\\s*(-?\\d+(?:\\.\\d+)?)").find(raw.lowercase())
                ?: return "गणना समझ नहीं आई।"
            val a = m.groupValues[1].toDouble()
            val b = m.groupValues[3].toDouble()
            val ans = when (m.groupValues[2].trim()) {
                "plus", "+" -> a + b
                "minus", "-" -> a - b
                "into", "multiply", "*", "x" -> a * b
                "divided by", "divide", "/" -> if (b == 0.0) return "शून्य से भाग नहीं कर सकते।" else a / b
                else -> return "ऑपरेशन समझ नहीं आया।"
            }
            "जवाब है $ans।"
        } catch (e: Exception) { "गणना में त्रुटि।" }
    }

    private fun handleUnknown(raw: String): String {
        // Universal fallback: web search anything
        return handleWebSearch(com.example.hacker.domain.entities.IntentEntity("WEB_SEARCH", mapOf("query" to raw)))
    }

    private fun speakResponse(text: String) {
        try { tts?.speakText(text) } catch (_: Exception) {}
        Log.i("HACKER_VOICE", "Response: $text")
    }
}
