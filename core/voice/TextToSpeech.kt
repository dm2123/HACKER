package com.example.hacker.core.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.Locale
import com.example.hacker.core.logging.HackerLogger

/** Text-to-Speech wrapper with Hindi/English support */
class TextToSpeechWrapper(private val context: Context) {

    private val logger = HackerLogger()
    private var tts: TextToSpeech? = null
    private var isReady = false
    private var pendingText: String? = null
    private var pendingLang: String? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isReady = true
                // Default to English, fallback to en-IN
                val res = tts?.setLanguage(Locale.ENGLISH) ?: TextToSpeech.LANG_NOT_SUPPORTED
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale.forLanguageTag("en-IN"))
                }
                pendingText?.let { speakText(it, pendingLang ?: "en"); pendingText = null }
            } else {
                logger.e("TTS", "Init failed: $status")
            }
        }
    }

    /** Speak text with given language (en/hi/hinglish) */
    fun speakText(text: String, language: String = "en") {
        if (text.isBlank()) return
        if (!isReady) {
            pendingText = text
            pendingLang = language
            return
        }
        val locale = when (language.lowercase()) {
            "hi", "hi-in", "hinglish" -> Locale.forLanguageTag("hi-IN")
            "en-in" -> Locale.forLanguageTag("en-IN")
            else -> Locale.ENGLISH
        }
        val langResult = tts?.setLanguage(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            logger.e("TTS", "Language not available: $language, fallback to EN")
            tts?.setLanguage(Locale.ENGLISH)
        }
        tts?.setSpeechRate(1.0f)
        tts?.setPitch(1.0f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hacker_${System.currentTimeMillis()}")
    }

    /** Stop speaking */
    fun stopSpeaking() {
        try { tts?.stop() } catch (_: Exception) {}
    }

    fun isSpeaking(): Boolean = try { tts?.isSpeaking == true } catch (_: Exception) { false }

    fun shutdown() {
        try { tts?.shutdown() } catch (_: Exception) {}
        tts = null
        isReady = false
    }
}
