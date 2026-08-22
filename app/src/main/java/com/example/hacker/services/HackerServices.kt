package com.example.hacker.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.voice.VoiceInteractionService
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.view.accessibility.AccessibilityEvent

/**
 * Voice Interaction Service (spec sections 4, 16).
 * Registered as the system voice interaction service.
 * When HACKER is set as the system assistant (ROLE_ASSISTANT),
 * this service handles assistant invocations.
 */
class HackerVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
    }
}

/**
 * Creates a session whenever the assistant is invoked
 * (long-press home / power / gesture — works from lock screen too).
 */
class HackerVoiceInteractionSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        return HackerVoiceInteractionSession(this)
    }
}

/**
 * HACKER 7.0 - Working assistant session, Siri-style:
 * shows an overlay above the lock screen, listens with SpeechRecognizer,
 * runs the command through CommandProcessor and speaks the reply.
 */
class HackerVoiceInteractionSession(context: Context) : VoiceInteractionSession(context) {

    private val handler = Handler(Looper.getMainLooper())
    private var speaker: com.example.hacker.voice.Speaker? = null
    private var recognizer: SpeechRecognizer? = null
    private var statusText: TextView? = null

    override fun onCreateContentView(): View {
        val density = resources.displayMetrics.density
        val pad = (20 * density).toInt()

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(0xEE101820.toInt())
        layout.setPadding(pad, pad, pad, pad)

        val title = TextView(context)
        title.text = "HACKER"
        title.setTextColor(0xFF00E676.toInt())
        title.textSize = 22f
        layout.addView(title)

        val status = TextView(context)
        status.text = "Sun raha hoon... bolo kya karna hai"
        status.setTextColor(0xFFE0E0E0.toInt())
        status.textSize = 16f
        status.setPadding(0, pad / 2, 0, 0)
        layout.addView(status)

        statusText = status
        return layout
    }

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        if (speaker == null) {
            speaker = com.example.hacker.voice.Speaker(context)
        }
        startListening()
    }

    override fun onHide() {
        super.onHide()
        stopListening()
    }

    override fun onDestroy() {
        stopListening()
        speaker?.shutdown()
        speaker = null
        super.onDestroy()
    }

    private fun setStatus(text: String) {
        statusText?.text = text
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            finishWith("Speech recognition available nahi hai.")
            return
        }
        stopListening()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                setStatus("Bolo...")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                setStatus("Samajh raha hoon...")
            }
            override fun onError(error: Int) {
                finishWith("Maaf kijiye, dobara try kijiye.")
            }
            override fun onResults(results: Bundle?) {
                val heard = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                if (heard.isNullOrBlank()) {
                    finishWith("Kuch sunayi nahi diya.")
                } else {
                    val reply = com.example.hacker.voice.CommandProcessor.handle(
                        context.applicationContext,
                        heard
                    )
                    finishWith(reply)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
        recognizer?.startListening(intent)
    }

    private fun stopListening() {
        try {
            recognizer?.stopListening()
            recognizer?.destroy()
        } catch (_: Exception) {
        }
        recognizer = null
    }

    private fun finishWith(message: String) {
        setStatus(message)
        speaker?.speak(message)
        handler.postDelayed({
            hide()
        }, 5000)
    }
}

/**
 * Notification Listener (spec section 10).
 * Active only after user explicitly grants access in Settings.
 * Persists notifications locally (spec 10: local-first).
 */
class HackerNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        if (sbn.packageName == packageName) return
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT)?.toString() ?: extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
        if (title.isBlank() && text.isBlank()) return
        val label = try { packageManager.getApplicationLabel(packageManager.getApplicationInfo(sbn.packageName, 0)).toString() } catch(_:Exception){ sbn.packageName }
        com.example.hacker.data.repository.NotificationRepository.save(this, sbn.packageName, label, title, text)
    }
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}

/**
 * Accessibility Service (spec section 11).
 * Legitimate accessibility assistance only. Enabled by explicit user action.
 */
class HackerAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
