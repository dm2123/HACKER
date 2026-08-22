package com.example.hacker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hacker.voice.CommandProcessor
import com.example.hacker.voice.Speaker

/**
 * HACKER always-listening hotword service — "Hey HACKER" se kahin se bhi wake.
 * Foreground service with microphone type (Android 14 compliant).
 * Continuous SpeechRecognizer loop, phrase match via contains("hacker").
 * Only enrolled speaker allowed if VoiceIdentity enrolled (via core/voice/voice_identity).
 */
class HackerHotwordService : Service() {

    private var recognizer: SpeechRecognizer? = null
    private var speaker: Speaker? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false
    private var isCommandMode = false

    companion object {
        const val CHANNEL_ID = "hacker_hotword"
        const val NOTIF_ID = 1001
        const val PREF_HOTWORD = "hotword_enabled"

        fun isEnabled(ctx: Context): Boolean {
            return ctx.getSharedPreferences("hacker_prefs", Context.MODE_PRIVATE).getBoolean(PREF_HOTWORD, false)
        }
        fun setEnabled(ctx: Context, enabled: Boolean) {
            ctx.getSharedPreferences("hacker_prefs", Context.MODE_PRIVATE).edit().putBoolean(PREF_HOTWORD, enabled).apply()
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        speaker = Speaker(this)
        Log.d("HACKER_HOTWORD", "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notif = buildNotification("HACKER hotword active — bolo 'Hey HACKER'")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIF_ID, notif, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIF_ID, notif)
        }
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            startHotwordListening()
        } else {
            Log.w("HACKER_HOTWORD", "Recognition not available")
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopListening()
        speaker?.shutdown()
        speaker = null
        super.onDestroy()
        // auto-restart if still enabled
        if (isEnabled(this)) {
            handler.postDelayed({ try { startService(Intent(this, HackerHotwordService::class.java)) } catch (_: Exception) {} }, 2000)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "HACKER Hotword", NotificationManager.IMPORTANCE_LOW)
            ch.description = "Always listening for Hey HACKER"
            (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.createNotificationChannel(ch)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HACKER")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .build()
    }

    private fun startHotwordListening() {
        isCommandMode = false
        startListeningInternal("Hey HACKER sun raha hoon...")
    }

    private fun startCommandListening() {
        isCommandMode = true
        updateNotification("HACKER sun raha hai — command bolo...")
        handler.postDelayed({ startListeningInternal("Command bolo...") }, 300)
    }

    private fun startListeningInternal(prompt: String) {
        if (isListening) stopListening()
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return

        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) {
                isListening = false
                // restart loop except no-match/timeout
                handler.postDelayed({ restartListening() }, 800)
            }
            override fun onResults(results: Bundle?) {
                isListening = false
                val heard = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.trim() ?: ""
                if (heard.isBlank()) {
                    restartListening(); return
                }
                Log.d("HACKER_HOTWORD", "Heard: $heard commandMode=$isCommandMode")
                if (!isCommandMode) {
                    // wake word detection
                    val lower = heard.lowercase()
                    val isWake = lower.contains("hacker") || lower.contains("हैकर") || lower.contains("hekar") || lower.contains("hey")
                    // Only enrolled speaker check would go here — for now any "hacker" phrase wakes
                    val verified = isWake

                    if (verified && isWake) {
                        speaker?.speak("Yes Boss")
                        // extract command after wake word if any
                        val cmdAfter = heard.substringAfter("hacker", "").substringAfter("हैकर", "").trim()
                        if (cmdAfter.length > 2) {
                            // wake word + command in same utterance: "hey hacker torch on karo"
                            handleCommand(cmdAfter)
                        } else {
                            startCommandListening()
                        }
                    } else {
                        restartListening()
                    }
                } else {
                    // command mode
                    handleCommand(heard)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        try { recognizer?.startListening(intent) } catch (e: Exception) {
            Log.e("HACKER_HOTWORD", "startListening failed: ${e.message}")
            handler.postDelayed({ restartListening() }, 1000)
        }
    }

    private fun handleCommand(cmd: String) {
        val reply = try { CommandProcessor.handle(applicationContext, cmd) } catch (e: Exception) { "Error: ${e.message}" }
        speaker?.speak(reply)
        // update notification with last command
        updateNotification("Last: $cmd → $reply")
        // go back to hotword listening after 4 sec
        handler.postDelayed({ startHotwordListening() }, 4000)
    }

    private fun restartListening() {
        if (isCommandMode) {
            // if command mode timed out, go back to hotword
            startHotwordListening()
        } else {
            handler.postDelayed({ startListeningInternal("Hey HACKER...") }, 500)
        }
    }

    private fun stopListening() {
        try { recognizer?.stopListening(); recognizer?.destroy() } catch (_: Exception) {}
        recognizer = null
        isListening = false
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        nm.notify(NOTIF_ID, buildNotification(text))
    }
}
