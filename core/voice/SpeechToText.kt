package com.example.hacker.core.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hacker.core.logging.HackerLogger

/** Speech-to-Text using Android SpeechRecognizer (offline capable where supported) */
class SpeechToText(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val resultLiveData = MutableLiveData<String>()
    private var recognitionRequested = false
    private val logger = HackerLogger()

    /** Start listening for speech input */
    fun startListening() {
        if (recognitionRequested) return
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            resultLiveData.value = "Speech recognition not available"
            return
        }
        recognitionRequested = true
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer = recognizer

        val recognitionIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "HACKER सुन रहा है...")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        recognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                logger.d("STT", "Ready for speech")
            }
            override fun onBeginningOfSpeech() {
                logger.d("STT", "Speech started")
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                logger.d("STT", "Speech ended")
                recognitionRequested = false
            }
            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech matched"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission denied"
                    else -> "Error: $error"
                }
                resultLiveData.value = errorMessage
                logger.e("STT", errorMessage)
                recognitionRequested = false
            }
            override fun onResults(results: Bundle?) {
                val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!list.isNullOrEmpty()) {
                    val bestResult = list[0]
                    resultLiveData.value = bestResult
                    logger.d("STT", "Recognized: $bestResult")
                }
                recognitionRequested = false
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        try {
            recognizer.startListening(recognitionIntent)
        } catch (e: Exception) {
            logger.e("STT", "startListening failed: ${e.message}")
            recognitionRequested = false
        }
    }

    /** Stop listening */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        recognitionRequested = false
    }

    /** Check if recognition is available */
    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /** Get result as LiveData */
    fun getResult(): LiveData<String> = resultLiveData

    /** Clean up resources */
    fun destroy() {
        try { speechRecognizer?.destroy() } catch (_: Exception) {}
        speechRecognizer = null
        recognitionRequested = false
    }
}
