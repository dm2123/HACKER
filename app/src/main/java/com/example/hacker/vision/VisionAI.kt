package com.example.hacker.vision

import android.content.Context

/**
 * HACKER 4.0 — Vision AI (spec section 21)
 * Offline placeholder: guides user to pick image/PDF for OCR/code analysis.
 * Full OCR needs ML Kit — this keeps build light and permission-gated.
 */
object VisionAI {

    fun handle(context: Context, raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("screenshot") || lower.contains("स्क्रीनशॉट") ->
                "Yes Boss, screenshot analysis: Tools > Vision me image pick karo — main OCR se text nikalke error/assignment summary bataunga."
            lower.contains("pdf") || lower.contains("document") || lower.contains("डॉक्यूमेंट") ->
                "Yes Boss, PDF/Doc analysis: File pick karo — main summary + important questions nikal dunga. Private docs on-device hi rahenge."
            lower.contains("image") || lower.contains("फोटो") || lower.contains("photo") ->
                "Yes Boss, image analysis: Photo pick karo — text extract ya code error detect kar dunga."
            lower.contains("code") && (lower.contains("photo") || lower.contains("image")) ->
                "Yes Boss, code photo se debugging: Image share karo, main error + fix bataunga."
            else -> "Vision AI: 'screenshot me error batao', 'pdf ka summary banao', 'image se questions nikalo' — bolo aur file pick karo."
        }
    }
}
