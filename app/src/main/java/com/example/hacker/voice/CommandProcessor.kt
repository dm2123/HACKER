package com.example.hacker.voice

import android.content.Context
import com.example.hacker.automation.AutomationEngine
import com.example.hacker.data.repository.ActivityLogRepository
import com.example.hacker.phone.DeviceActions
import com.example.hacker.phonecontrol.AlarmController
import com.example.hacker.phonecontrol.AppLauncher
import com.example.hacker.phonecontrol.Contacts
import com.example.hacker.phonecontrol.Dialer
import com.example.hacker.phonecontrol.DeviceInfo
import com.example.hacker.phonecontrol.SettingsLauncher
import com.example.hacker.phonecontrol.SmsController
import com.example.hacker.phonecontrol.TimerController
import com.example.hacker.phonecontrol.TorchController
import com.example.hacker.phonecontrol.VolumeController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CommandProcessor {

    fun handle(context: Context, raw: String): String {
        val text = raw.trim()
        val lower = text.lowercase()
        if (text.isEmpty()) return "कुछ नहीं सुना, दोबारा बोलो।"

        var action = "unknown"

        val response = when {
            lower.contains("time") || lower.contains("समय") || lower.contains("टाइम") -> {
                action = "time"
                "अभी समय है " + SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            }

            lower.contains("date") || lower.contains("तारीख") || lower.contains("डेट") || lower.contains("आज") -> {
                action = "date"
                "आज की तारीख है " + SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
            }

            lower.contains("battery") || lower.contains("बैटरी") || lower.contains("चार्ज") -> {
                action = "battery"
                "बैटरी " + DeviceInfo.batteryLevel(context) + "% है। " + DeviceInfo.batteryStatus(context)
            }

            lower.contains("torch") || lower.contains("flashlight") || lower.contains("टॉर्च") ||
                lower.contains("लाइट") || lower.contains("फ्लैश") -> {
                action = "torch"
                TorchController.toggle(context)
                if (TorchController.isOn(context)) "टॉर्च ऑन कर दिया।" else "टॉर्च बंद कर दिया।"
            }

            lower.contains("volume up") || lower.contains("आवाज़ बढ़ा") || lower.contains("आवाज बढ़ा") ||
                lower.contains("वॉल्यूम अप") -> {
                action = "volume_up"
                VolumeController.up(context)
                "आवाज़ बढ़ा दी।"
            }

            lower.contains("volume down") || lower.contains("आवाज़ घटा") || lower.contains("आवाज घटा") ||
                lower.contains("वॉल्यूम डाउन") -> {
                action = "volume_down"
                VolumeController.down(context)
                "आवाज़ घटा दी।"
            }

            lower.startsWith("call") || lower.contains("कॉल") || lower.contains("फोन कर") -> {
                action = "call"
                val target = text.substringAfter("call", "").substringAfter("कॉल", "")
                    .substringAfter("फोन कर", "").trim()
                if (target.isEmpty()) {
                    "किसे कॉल करूँ? नाम या नंबर बोलो।"
                } else {
                    val num = if (target.all { it.isDigit() || it == '+' || it == '-' || it == ' ' })
                        target else (Contacts.lookupNumber(context, target) ?: target)
                    Dialer.call(context, num.replace(" ", ""))
                }
            }

            lower.startsWith("sms") || lower.startsWith("message") || lower.contains("संदेश") ||
                lower.contains("मैसेज") || lower.contains("msg") -> {
                action = "sms"
                SmsController.open(context)
                "मैसेज ऐप खोल रहा हूँ।"
            }

            lower.startsWith("open ") || lower.contains("खोलो") || lower.contains("ओपन") -> {
                action = "open_app"
                val app = text.substringAfter("open", "").substringAfter("खोलो", "")
                    .substringAfter("ओपन", "").trim()
                if (app.isEmpty()) "कौन सा ऐप खोलूँ?"
                else if (AppLauncher.open(context, app)) "$app खोल दिया।"
                else "$app ऐप नहीं मिला।"
            }

            lower.contains("wifi") || lower.contains("वाईफाई") || lower.contains("wi-fi") -> {
                action = "wifi"
                SettingsLauncher.wifi(context)
                "वाईफाई सेटिंग्स खोल रहा हूँ।"
            }

            lower.contains("bluetooth") || lower.contains("ब्लूटूथ") -> {
                action = "bluetooth"
                SettingsLauncher.bluetooth(context)
                "ब्लूटूथ सेटिंग्स खोल रहा हूँ।"
            }

            lower.contains("youtube") || lower.contains("यूट्यूब") -> {
                action = "youtube"
                val q = text.substringAfter("youtube", "").substringAfter("यूट्यूब", "").trim()
                if (q.isEmpty()) {
                    AppLauncher.open(context, "youtube")
                    "YouTube खोल रहा हूँ।"
                } else {
                    DeviceActions.youtubeSearch(context, q)
                    "YouTube पर खोज रहा हूँ: $q"
                }
            }

            lower.startsWith("search ") || lower.contains("खोजो") || lower.startsWith("google") ||
                lower.contains("गूगल") -> {
                action = "web"
                val q = text.substringAfter("search", "").substringAfter("खोजो", "")
                    .substringAfter("google", "").trim()
                if (q.isEmpty()) "क्या खोजूँ?"
                else {
                    DeviceActions.webSearch(context, q)
                    "गूगल पर खोज रहा हूँ: $q"
                }
            }

            lower.contains("alarm") || lower.contains("अलार्म") || lower.contains("अलार्म लगा") -> {
                action = "alarm"
                val hr = extractHour(lower)
                AlarmController.setAlarm(context, hr, 0)
                "अलार्म सेट कर दिया $hr बजे।"
            }

            lower.contains("timer") || lower.contains("टाइमर") -> {
                action = "timer"
                val mins = extractMinutes(lower)
                val secs = if (mins > 0) mins * 60 else 60
                TimerController.start(context, secs)
                "टाइमर शुरू कर दिया ($secs सेकंड)।"
            }

            lower.contains("study mode") || lower.contains("स्टडी मोड") -> {
                action = "automation"
                AutomationEngine.run(context, "study mode")
            }

            lower.contains("sleep mode") || lower.contains("स्लीप मोड") -> {
                action = "automation"
                AutomationEngine.run(context, "sleep mode")
            }

            lower.contains("morning routine") || lower.contains("मॉर्निंग रूटीन") -> {
                action = "automation"
                AutomationEngine.run(context, "morning routine")
            }

            lower.contains("work mode") || lower.contains("वर्क मोड") -> {
                action = "automation"
                AutomationEngine.run(context, "work mode")
            }

            lower.contains("hello") || lower.contains("hi ") || lower.contains("नमस्ते") ||
                lower.contains("नमस्कार") || lower.contains("hey") || lower.contains("हैलो") -> {
                "नमस्ते! मैं HACKER हूँ — time, battery, torch, call, open, wifi, alarm, study mode आज़माओ।"
            }

            lower.contains("who are you") || lower.contains("तुम कौन") || lower.contains("तेरा नाम") ||
                lower.contains("your name") -> "मैं HACKER हूँ, आपका वॉयस असिस्टेंट।"

            lower.contains("thank") || lower.contains("धन्यवाद") || lower.contains("शुक्रिया") ->
                "आपका स्वागत है!"

            lower.contains("notification") || lower.contains("नोटिफिकेशन") || lower.contains("नोटिस") -> {
                action = "notifications"
                val repo = com.example.hacker.data.repository.NotificationRepository(context)
                // quick sync read last 3 (blocking via runBlocking not allowed) -> do async? For now return guidance
                // Also handle "clear notifications"
                if (lower.contains("clear") || lower.contains("साफ") || lower.contains("हटा")) {
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch { repo.clear() }
                    "Notifications साफ कर दीं।"
                } else "Notifications देखने के लिए Notifications screen खोलो — या कहो 'clear notifications'।"
            }

            lower.contains("remember") || lower.contains("yaad rakh") || lower.contains("याद रख") -> {
                action = "memory_save"
                val what = text.substringAfter("remember", "").substringAfter("yaad rakh", "").substringAfter("याद रख", "").trim().ifEmpty { text }
                if (what.length < 3) "क्या याद रखूँ?"
                else {
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch { com.example.hacker.data.repository.MemoryRepository(context).add("voice", what) }
                    "याद रख लिया: $what"
                }
            }

            lower.contains("brightness") || lower.contains("ब्राइटनेस") || lower.contains("चमक") -> {
                action = "brightness"
                DeviceActions.openApp(context, "settings")
                "Brightness settings खोल रहा हूँ।"
            }

            lower.contains("dnd") || lower.contains("do not disturb") || lower.contains("डिस्टर्ब") -> {
                action = "dnd"
                DeviceActions.openApp(context, "settings")
                "Do Not Disturb settings खोल रहा हूँ।"
            }

            lower.contains("screenshot") || lower.contains("स्क्रीनशॉट") -> {
                action = "screenshot"
                "Screenshot के लिए power + volume down दबाओ — या Tools में जाओ।"
            }

            lower.contains("map") || lower.contains("maps") || lower.contains("नक्शा") || lower.contains("लोकेशन") -> {
                action = "maps"
                val q = text.substringAfter("map", "").substringAfter("नक्शा", "").trim()
                if (q.isNotBlank()) DeviceActions.webSearch(context, "$q maps")
                else DeviceActions.openApp(context, "maps")
                if (q.isNotBlank()) "Maps पर $q खोज रहा हूँ।" else "Maps खोल रहा हूँ।"
            }

            else -> {
                // fuzzy app open: if text is single word maybe app name
                val fuzzy = fuzzyAppMatch(context, lower)
                if (fuzzy != null) {
                    action = "open_app_fuzzy"
                    com.example.hacker.phonecontrol.AppLauncher.open(context, fuzzy)
                    "$fuzzy खोल रहा हूँ (fuzzy match) — अगली बार 'open $fuzzy' बोलो।"
                } else "कमांड समझ नहीं आई: '$text'। कोशिश करो — time, battery, torch, call मम्मी, open whatsapp, wifi, alarm 6, study mode, notifications batao, yaad rakh <baat>।"
            }
        }

        ActivityLogRepository(context).log(text, action, response)
        return response
    }

    private fun fuzzyAppMatch(context: Context, lower: String): String? {
        // simple Levenshtein against installed app labels (first 30)
        return try {
            val pm = context.packageManager
            val apps = pm.getInstalledApplications(0).take(80).mapNotNull { try { pm.getApplicationLabel(it).toString().lowercase() } catch(_:Exception){null} }
            val query = lower.trim().take(20)
            if (query.length < 3) return null
            var best: String? = null; var bestDist = 3
            for (app in apps) {
                if (app.contains(query) || query.contains(app)) return app
                val d = levenshtein(query, app.take(query.length+2))
                if (d < bestDist) { bestDist = d; best = app }
            }
            best
        } catch(_:Exception){ null }
    }
    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length+1){ IntArray(b.length+1) }
        for (i in 0..a.length) dp[i][0]=i
        for (j in 0..b.length) dp[0][j]=j
        for (i in 1..a.length) for (j in 1..b.length) dp[i][j]= if(a[i-1]==b[j-1]) dp[i-1][j-1] else 1+minOf(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])
        return dp[a.length][b.length]
    }

    private fun extractHour(lower: String): Int {
        val m = Regex("(\\d+)\\s*(am|pm|बजे|बज|am|pm)?").find(lower)
        var h = m?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 6
        val suf = m?.groupValues?.getOrNull(2)?.lowercase() ?: ""
        if (suf == "pm" && h < 12) h += 12
        if (suf == "am" && h == 12) h = 0
        return h
    }

    private fun extractMinutes(lower: String): Int {
        val m = Regex("(\\d+)\\s*(minute|min|मिनट|minutes)").find(lower)
        return m?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 1
    }
}
