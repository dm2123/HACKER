package com.example.hacker.core.voice

import android.speech.tts.TextToSpeech
import android.content.Context
import java.util.Locale
import com.example.hacker.core.logging.HackerLogger

/** Text-to-Speech conversion for HACKER responses */
class TextToSpeechWrapper(private val context: Context) : TextToSpeech(context, object : TextToSpeech.OnInitListener {
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set default language (Hinglish/English support)
            val result = setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                setLanguage(Locale.forLanguageTag("en-IN"))
            }
        }
    }
}) {

    private val logger = HackerLogger()
    private var isSpeaking = false

    /** Speak text with given language */
    fun speakText(text: String, language: String = "en") {
        if (isSpeaking) {
            stopSpeaking()
        }

        isSpeaking = true
        val langs = when (language) {
            "en" -> Locale.ENGLISH
            "hi" -> Locale.forLanguageTag("hi-IN")
            "hi-IN" -> Locale.forLanguageTag("hi-IN")
            "en-IN" -> Locale.forLanguageTag("en-IN")
            else -> Locale.ENGLISH
        }

        val ttsResult = setLanguage(langs)
        if (ttsResult == TextToSpeech.LANG_AVAILABLE || ttsResult == TextToSpeech.LARN_MISSING_DATA) {
            // Set speech rate and pitch
            setSpeechRate(1.0f)
            setPitch(1.0f)

            // Speak the text
            speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            logger.e("TTS", "Language not available: $language")
            isSpeaking = false
        }
    }

    /** Set speech language */
    private fun setLanguage(locale: Locale): Int {
        return setLanguage(locale)
    }

    /** Get current language result */
    fun getLanguageResult(): Int = super.onInit(TextToSpeech.SUCCESS)

    /** Stop speaking */
    fun stopSpeaking() {
        if (speechSynthesizer != null && isSpeaking) {
            speechSynthesizer.stop()
            isSpeaking = false
        }
    }

    /** Speak interrupt */
    fun speak(text: String, queueMode: Int, params: Bundle?, nullParams: String?) {
        if (text.isNotEmpty() && speechSynthesizer != null) {
            speechSynthesizer.speak(text, queueMode, params, nullParams)
        }
    }

    /** Get current speech engine status */
    fun isSpeakingStatus(): Boolean = isSpeaking
}