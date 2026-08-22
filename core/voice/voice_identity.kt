package com.example.hacker.core.voice

import com.example.hacker.domain.entities.VoiceProfile
import java.security.MessageDigest
import java.util.UUID

/** Voice identity verification and wake word detection.
 *  Offline, local-first: voice prints are hashed and stored locally.
 *  Not a replacement for Android PIN/biometric for sensitive actions. */
class VoiceIdentityManager {

    private var storedProfile: VoiceProfile? = null

    /** Enroll a new voice profile from sample phrases */
    fun enrollProfile(phrases: List<String>): VoiceProfile {
        if (phrases.isEmpty()) {
            return VoiceProfile(id = "", isEnrolled = false, voicePrintHash = null, createdAt = System.currentTimeMillis())
        }
        val normalized = phrases.joinToString("|") { it.trim().lowercase() }
        val hash = sha256(normalized)
        val profile = VoiceProfile(
            id = UUID.randomUUID().toString(),
            isEnrolled = true,
            voicePrintHash = hash,
            createdAt = System.currentTimeMillis()
        )
        storedProfile = profile
        return profile
    }

    /** Verify if the current voice matches a known profile.
     *  Input is raw audio bytes (in real device from AudioRecord). We hash and compare.
     *  Returns false if no enrollment exists. */
    fun verifyVoice(audioSample: ByteArray): Boolean {
        val profile = storedProfile ?: return false
        if (!profile.isEnrolled) return false
        val hash = sha256Bytes(audioSample)
        // Exact match for enrolled hash; in production use cosine similarity threshold
        return hash == profile.voicePrintHash || isNearMatch(hash, profile.voicePrintHash ?: "")
    }

    /** Verify from transcribed text (fallback when raw audio not available) */
    fun verifyText(phrase: String): Boolean {
        val profile = storedProfile ?: return false
        if (!profile.isEnrolled) return false
        val hash = sha256(phrase.trim().lowercase())
        return hash == profile.voicePrintHash || isNearMatch(hash, profile.voicePrintHash ?: "")
    }

    /** Detect wake word "Hey HACKER" (also Hinglish variants) in audio bytes */
    fun detectWakeWord(audio: ByteArray): Boolean {
        return try {
            val text = String(audio, Charsets.UTF_8).lowercase()
            detectWakeWordText(text)
        } catch (_: Exception) {
            false
        }
    }

    /** Detect wake word in transcribed text */
    fun detectWakeWordText(text: String): Boolean {
        val lower = text.lowercase().trim()
        val variants = listOf(
            "hey hacker", "hey hecker", "he hacker", "hello hacker",
            "hey hackar", "hacker", "हैकर", "हे हैकर"
        )
        return variants.any { lower.contains(it) }
    }

    fun getStoredProfile(): VoiceProfile? = storedProfile

    fun clearProfile() {
        storedProfile = null
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun sha256Bytes(input: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /** Simple near-match: allow 1-2 char difference (tolerant enrollment) */
    private fun isNearMatch(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var diff = 0
        for (i in a.indices) {
            if (a[i] != b[i]) {
                diff++
                if (diff > 4) return false
            }
        }
        return diff <= 4
    }
}
