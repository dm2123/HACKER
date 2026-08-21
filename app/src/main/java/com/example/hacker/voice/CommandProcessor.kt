package com.example.hacker.voice

import android.content.Context
import android.os.Build
import com.example.hacker.phone.DeviceActions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommandProcessor {

    fun handle(context: Context, raw: String): String {
        val text = raw.trim()
        val lower = text.lowercase()
        if (text.isEmpty()) return "कुछ नहीं सुना, दोबारा बोलो।"

        // TIME
        if (lower.contains("time") || lower.contains("समय") || lower.contains("टाइम")) {
            val t = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            return "अभी समय है $t"
        }

        // DATE
        if (lower.contains("date") || lower.contains("तारीख") || lower.contains("डेट") || lower.contains("आज")) {
            val d = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
            return "आज की तारीख है $d"
        }

        // BATTERY
        if (lower.contains("battery") || lower.contains("बैटरी") || lower.contains("चार्ज")) {
            val level = DeviceActions.batteryLevel(context)
            val status = DeviceActions.batteryStatus(context)
            return "बैटरी $level प्रतिशत है। स्थिति: $status।"
        }

        // TORCH / FLASHLIGHT
        if (lower.contains("torch") || lower.contains("flashlight") || lower.contains("टॉर्च") ||
            lower.contains("लाइट") || lower.contains("फ्लैश")
        ) {
            val on = DeviceActions.toggleTorch(context)
            return if (on) "टॉर्च ऑन कर दिया।" else "टॉर्च बंद कर दिया।"
        }

        // VOLUME UP
        if (lower.contains("volume up") || lower.contains("आवाज़ बढ़ा") || lower.contains("आवाज बढ़ा") ||
            lower.contains("वॉल्यूम अप")
        ) {
            DeviceActions.volumeUp(context)
            return "आवाज़ बढ़ा दी।"
        }

        // VOLUME DOWN
        if (lower.contains("volume down") || lower.contains("आवाज़ घटा") || lower.contains("आवाज घटा") ||
            lower.contains("वॉल्यूम डाउन")
        ) {
            DeviceActions.volumeDown(context)
            return "आवाज़ घटा दी।"
        }

        // CALL
        if (lower.startsWith("call") || lower.contains("कॉल") || lower.contains("फोन कर")) {
            val target = text
                .substringAfter("call", "")
                .substringAfter("कॉल", "")
                .substringAfter("फोन कर", "")
                .trim()
            if (target.isEmpty()) return "किसे कॉल करूँ? नाम या नंबर बोलो।"
            val isDigits = target.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
            val number = if (isDigits) target else (DeviceActions.contactNumber(context, target) ?: target)
            return try {
                DeviceActions.callNumber(context, number.replace(" ", ""))
                "कॉल कर रहा हूँ $target"
            } catch (e: Exception) {
                DeviceActions.dialNumber(context, number.replace(" ", ""))
                "डायलर खोल रहा हूँ $target"
            }
        }

        // SMS
        if (lower.startsWith("sms") || lower.startsWith("message") || lower.contains("संदेश") ||
            lower.contains("मैसेज") || lower.contains("msg")
        ) {
            DeviceActions.openSmsApp(context)
            return "मैसेज ऐप खोल रहा हूँ।"
        }

        // OPEN APP
        if (lower.startsWith("open ") || lower.contains("खोलो") || lower.contains("ओपन")) {
            val app = text
                .substringAfter("open", "")
                .substringAfter("खोलो", "")
                .substringAfter("ओपन", "")
                .trim()
            if (app.isEmpty()) return "कौन सा ऐप खोलूँ?"
            return if (DeviceActions.openApp(context, app)) {
                "$app खोल दिया।"
            } else {
                "$app ऐप नहीं मिला।"
            }
        }

        // WIFI
        if (lower.contains("wifi") || lower.contains("वाईफाई") || lower.contains("wi-fi")) {
            DeviceActions.openWifi(context)
            return "वाईफाई सेटिंग्स खोल रहा हूँ।"
        }

        // BLUETOOTH
        if (lower.contains("bluetooth") || lower.contains("ब्लूटूथ")) {
            DeviceActions.openBluetooth(context)
            return "ब्लूटूथ सेटिंग्स खोल रहा हूँ।"
        }

        // YOUTUBE
        if (lower.contains("youtube") || lower.contains("यूट्यूब")) {
            val q = text.substringAfter("youtube", "").substringAfter("यूट्यूब", "").trim()
            if (q.isEmpty()) {
                DeviceActions.openApp(context, "youtube")
                return "YouTube खोल रहा हूँ।"
            }
            DeviceActions.youtubeSearch(context, q)
            return "YouTube पर खोज रहा हूँ: $q"
        }

        // WEB SEARCH
        if (lower.startsWith("search ") || lower.contains("खोजो") || lower.startsWith("google") ||
            lower.contains("गूगल")
        ) {
            val q = text
                .substringAfter("search", "")
                .substringAfter("खोजो", "")
                .substringAfter("google", "")
                .trim()
            if (q.isEmpty()) return "क्या खोजूँ?"
            DeviceActions.webSearch(context, q)
            return "गूगल पर खोज रहा हूँ: $q"
        }

        // GREETING
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("हैलो") ||
            lower.contains("नमस्ते") || lower.contains("नमस्कार") || lower.contains("hey")
        ) {
            return "नमस्ते! मैं HACKER हूँ। कमांड दो — time, battery, torch, call, open, wifi, search।"
        }

        // WHO ARE YOU
        if (lower.contains("who are you") || lower.contains("तुम कौन") || lower.contains("तेरा नाम") ||
            lower.contains("your name")
        ) {
            return "मैं HACKER हूँ, आपका वॉयस असिस्टेंट।"
        }

        // THANKS
        if (lower.contains("thank") || lower.contains("धन्यवाद") || lower.contains("शुक्रिया")) {
            return "आपका स्वागत है!"
        }

        return "कमांड समझ नहीं आई: '$text'। कोशिश करो — time, battery, torch, call मम्मी, open whatsapp, wifi, search cricket।"
    }
}
