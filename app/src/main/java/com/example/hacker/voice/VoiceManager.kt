package com.example.hacker.voice

import android.content.Context
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

/**
 * HACKER Voice Setup (spec 4, 5)
 * STT: Hindi/English/Hinglish auto-detect + confidence
 * TTS: Natural voice with pitch/speed control
 */
class VoiceManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    
    private val _listeningState = MutableStateFlow(false)
    val listeningState: Flow<Boolean> = _listeningState
    
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: Flow<String> = _recognizedText
    
    private val _confidence = MutableStateFlow(0f)
    val confidence: Flow<Float> = _confidence

    init {
        // Initialize TTS
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Set voice properties
                textToSpeech?.language = Locale("hi", "IN") // Default Hindi
                textToSpeech?.pitch = 1.0f
                textToSpeech?.setSpeechRate(0.9f)
                Log.d("VOICE", "TTS initialized — Hindi voice ready")
            }
        }
        
        // Initialize STT
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    }

    /**
     * Start listening — auto-detect language
     */
    fun startListening(onResult: (String, Float, String) -> Unit) {
        if (speechRecognizer == null) return
        
        _listeningState.value = true
        
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                Log.d("VOICE", "Ready to listen")
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                _listeningState.value = false
            }
            override fun onError(error: Int) {
                Log.e("VOICE", "STT error: $error")
                _listeningState.value = false
            }
            override fun onResults(results: android.os.Bundle?) {
                _listeningState.value = false
                val matches = results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION) ?: emptyList()
                val confidences = results?.getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES) ?: floatArrayOf()
                
                if (matches.isNotEmpty()) {
                    val text = matches[0]
                    val conf = if (confidences.isNotEmpty()) confidences[0] else 0.5f
                    val lang = detectLanguage(text)
                    
                    _recognizedText.value = text
                    _confidence.value = conf
                    
                    Log.d("VOICE", "Heard: $text | Confidence: $conf | Language: $lang")
                    onResult(text, conf, lang)
                }
            }
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        })

        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN") // Start with Hindi
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("VOICE", "Failed to start listening: ${e.message}")
            _listeningState.value = false
        }
    }

    /**
     * Stop listening
     */
    fun stopListening() {
        speechRecognizer?.stopListening()
        _listeningState.value = false
    }

    /**
     * Speak reply — with natural pauses
     */
    fun speak(text: String, language: String = "hi") {
        if (textToSpeech == null || !textToSpeech!!.isSpeaking.not()) {
            // Set language
            val locale = when {
                language.contains("hi", ignoreCase = true) -> Locale("hi", "IN")
                language.contains("en", ignoreCase = true) -> Locale("en", "IN")
                else -> Locale("hi", "IN")
            }
            textToSpeech?.language = locale
            
            // Speak with natural prosody
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            Log.d("VOICE", "Speaking: $text")
        }
    }

    /**
     * Set voice properties
     */
    fun setVoiceProperties(pitch: Float = 1.0f, speed: Float = 0.9f) {
        textToSpeech?.pitch = pitch
        textToSpeech?.setSpeechRate(speed)
        Log.d("VOICE", "Voice set — pitch: $pitch, speed: $speed")
    }

    /**
     * Language detection — simple heuristic
     */
    private fun detectLanguage(text: String): String {
        val hindiMarkers = listOf("का", "की", "को", "से", "ने", "है", "हूँ", "करो", "बोलो", "खोलो", "दिखाओ")
        val englishMarkers = listOf("torch", "camera", "open", "play", "send", "the", "a", "and", "or")
        
        val hindiCount = hindiMarkers.count { text.contains(it, ignoreCase = true) }
        val englishCount = englishMarkers.count { text.contains(it, ignoreCase = true) }
        
        return when {
            hindiCount > englishCount -> "hindi"
            englishCount > hindiCount -> "english"
            else -> "hinglish"
        }
    }

    fun shutdown() {
        speechRecognizer?.destroy()
        textToSpeech?.shutdown()
    }
}
