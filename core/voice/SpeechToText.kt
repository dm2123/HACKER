package com.example.hacker.core.voice

import android.speech.RecognitionIntent
import android.speech.SpeechRecognizer
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hacker.core.logging.HackerLogger

/** Speech-to-Text conversion using Android SpeechRecognizer */
class SpeechToText(private val context: android.content.Context) {
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val resultLiveData = MutableLiveData<String>()
    private var recognitionRequested = false

    private val logger = HackerLogger()

    /** Start listening for speech input */
    fun startListening() {
        if (recognitionRequested) {
            return
        }

        recognitionRequested = true
        val recognitionIntent = RecognitionIntent().apply {
            setAction(RecognitionIntent.ACTION_RECOGNIZE_SPEECH)
            putExtra(RecognitionIntent.EXTRA_LANGUAGE_MODEL, RecognitionIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognitionIntent.EXTRA_PROMPT, "Speak now...")
            putExtra(RecognitionIntent.EXTRA_MAX_RESULTS, 3)
        }

        speechRecognizer.setRecognitionListener(object : android.speech.SpeechRecognizer.RecognitionListener {
            override fun onResults(results: Bundle) {
                val resultsList = results.getStringArrayList(SpeechRecognizer.RESULT_RECOGNITION)
                if (resultsList != null && resultsList.isNotEmpty()) {
                    val bestResult = resultsList[0]
                    resultLiveData.value = bestResult
                    logger.d("STT", "Recognized: $bestResult")
                }
            }

            override fun onBeginningOfSpeech() {
                logger.d("STT", "Speech started")
                resultLiveData.value = ""
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Optional: track audio volume
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech matched"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    else -> "Error: $error"
                }
                resultLiveData.value = errorMessage
                logger.e("STT", errorMessage)
                recognitionRequested = false
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // Optional: handle raw audio buffer
            }

            override fun onEndOfSpeech() {
                logger.d("STT", "Speech ended")
                recognitionRequested = false
            }

            override fun onReadyForSpeech(params: Bundle) {
                logger.d("STT", "Ready for speech")
                resultLiveData.value = ""
            }
        })

        speechRecognizer.setLanguage(android.util.LocaleLanguage("en-IN"))
        speechRecognizer.startListening(recognitionIntent)
    }

    /** Stop listening */
    fun stopListening() {
        if (speechRecognizer != null && recognitionRequested) {
            speechRecognizer.stopListening()
            recognitionRequested = false
        }
    }

    /** Check if recognition is available */
    fun isRecognitionAvailable(): Boolean {
        val intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        return context.packageManager.resolveActivity(intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY) != null
    }

    /** Get result as LiveData */
    fun getResult(): LiveData<String> {
        return resultLiveData
    }

    /** Clean up resources */
    fun destroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy()
        }
    }
}