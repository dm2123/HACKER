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
import com.example.hacker.voice.ResponseEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

            lower.contains("camera") || lower.contains("कैमरा") || lower.contains("photo") || lower.contains("फोटो") -> {
                action = "camera"
                DeviceActions.openCamera(context)
                "Camera खोल रहा हूँ।"
            }

            lower.contains("clipboard") || lower.contains("क्लिपबोर्ड") || lower.contains("copy") -> {
                action = "clipboard"
                val txt = text.substringAfter("clipboard", "").substringAfter("copy", "").trim()
                if (txt.isNotBlank()) {
                    DeviceActions.copyToClipboard(context, txt)
                    "Copy कर दिया।"
                } else {
                    val paste = DeviceActions.clipboardText(context)
                    "Clipboard: $paste"
                }
            }

            lower.contains("calendar") || lower.contains("कैलेंडर") || lower.contains("schedule") -> {
                action = "calendar"
                DeviceActions.openCalendar(context)
                "Calendar खोल रहा हूँ।"
            }

            lower.contains("whatsapp") || lower.contains("व्हाट्सएप") -> {
                action = "whatsapp"
                val msg = text.substringAfter("whatsapp", "").substringAfter("व्हाट्सएप", "").trim()
                if (msg.contains("message") || msg.contains("भेजो")) {
                    val contact = msg.substringBefore("message").substringBefore("भेजो").trim()
                    if (contact.isNotBlank()) {
                        DeviceActions.openApp(context, "whatsapp")
                        "WhatsApp खोल रहा हूँ — $contact को message भेजो।"
                    } else {
                        DeviceActions.openApp(context, "whatsapp")
                        "WhatsApp खोल रहा हूँ।"
                    }
                } else {
                    DeviceActions.openApp(context, "whatsapp")
                    "WhatsApp खोल रहा हूँ।"
                }
            }

            lower.contains("instagram") || lower.contains("insta") || lower.contains("इंस्टाग्राम") -> {
                action = "instagram"
                DeviceActions.openApp(context, "instagram")
                "Instagram खोल रहा हूँ।"
            }

            lower.contains("facebook") || lower.contains("fb") || lower.contains("फेसबुक") -> {
                action = "facebook"
                DeviceActions.openApp(context, "facebook")
                "Facebook खोल रहा हूँ।"
            }

            lower.contains("twitter") || lower.contains("x app") || lower.contains("ट्विटर") -> {
                action = "twitter"
                DeviceActions.openApp(context, "twitter")
                "Twitter खोल रहा हूँ।"
            }

            lower.contains("telegram") || lower.contains("टेलीग्राम") -> {
                action = "telegram"
                DeviceActions.openApp(context, "telegram")
                "Telegram खोल रहा हूँ।"
            }

            lower.contains("spotify") || lower.contains("स्पॉटिफाई") -> {
                action = "spotify"
                val song = text.substringAfter("spotify", "").substringAfter("स्पॉटिफाई", "").trim()
                DeviceActions.openApp(context, "spotify")
                if (song.isNotBlank()) "Spotify पर $song खोज रहा हूँ।" else "Spotify खोल रहा हूँ।"
            }

            lower.contains("gaana") || lower.contains("गाना app") -> {
                action = "gaana"
                DeviceActions.openApp(context, "gaana")
                "Gaana app खोल रहा हूँ।"
            }

            lower.contains("gmail") || lower.contains("email") || lower.contains("ईमेल") -> {
                action = "gmail"
                DeviceActions.openApp(context, "gmail")
                "Gmail खोल रहा हूँ।"
            }

            lower.contains("chrome") || lower.contains("browser") || lower.contains("ब्राउज़र") -> {
                action = "chrome"
                val url = text.substringAfter("chrome", "").substringAfter("browser", "").trim()
                if (url.isNotBlank()) DeviceActions.webSearch(context, url)
                else DeviceActions.openApp(context, "chrome")
                if (url.isNotBlank()) "Chrome में $url खोल रहा हूँ।" else "Chrome खोल रहा हूँ।"
            }

            lower.contains("photos") || lower.contains("gallery") || lower.contains("फोटो") && !lower.contains("photo khich") -> {
                action = "gallery"
                DeviceActions.openApp(context, "photos")
                "Gallery खोल रहा हूँ।"
            }

            lower.contains("settings") || lower.contains("सेटिंग्स") -> {
                action = "settings"
                DeviceActions.openApp(context, "settings")
                "Settings खोल रहा हूँ।"
            }

            lower.contains("clock") || lower.contains("घड़ी") -> {
                action = "clock"
                DeviceActions.openApp(context, "clock")
                "Clock खोल रहा हूँ।"
            }

            lower.contains("contacts") || lower.contains("संपर्क") -> {
                action = "contacts"
                DeviceActions.openApp(context, "contacts")
                "Contacts खोल रहा हूँ।"
            }

            lower.contains("files") || lower.contains("file manager") || lower.contains("फाइल") -> {
                action = "files"
                DeviceActions.openApp(context, "files")
                "File Manager खोल रहा हूँ।"
            }

            lower.contains("play store") || lower.contains("प्ले स्टोर") -> {
                action = "playstore"
                DeviceActions.openApp(context, "play store")
                "Play Store खोल रहा हूँ।"
            }

            lower.contains("map") || lower.contains("maps") || lower.contains("नक्शा") || lower.contains("लोकेशन") -> {
                action = "maps"
                val q = text.substringAfter("map", "").substringAfter("नक्शा", "").trim()
                if (q.isNotBlank()) DeviceActions.webSearch(context, "$q maps")
                else DeviceActions.openApp(context, "maps")
                if (q.isNotBlank()) "Maps पर $q खोज रहा हूँ।" else "Maps खोल रहा हूँ।"
            }

            lower.contains("rotate") || lower.contains("rotation") || lower.contains("घुमाओ") -> {
                action = "rotation"
                DeviceActions.openApp(context, "settings")
                "Screen rotation settings खोल रहा हूँ।"
            }

            lower.contains("airplane") || lower.contains("flight mode") || lower.contains("एयरप्लेन") -> {
                action = "airplane"
                DeviceActions.openApp(context, "settings")
                "Airplane mode settings खोल रहा हूँ।"
            }

            lower.contains("hotspot") || lower.contains("हॉटस्पॉट") -> {
                action = "hotspot"
                DeviceActions.openApp(context, "settings")
                "Hotspot settings खोल रहा हूँ।"
            }

            lower.contains("mobile data") || lower.contains("data") && !lower.contains("date") -> {
                action = "mobile_data"
                DeviceActions.openApp(context, "settings")
                "Mobile data settings खोल रहा हूँ।"
            }

            lower.contains("location") || lower.contains("gps") || lower.contains("लोकेशन सेटिंग") -> {
                action = "location_settings"
                DeviceActions.openApp(context, "settings")
                "Location settings खोल रहा हूँ।"
            }

            lower.contains("sound") || lower.contains("audio") || lower.contains("आवाज़ सेटिंग") -> {
                action = "sound_settings"
                DeviceActions.openApp(context, "settings")
                "Sound settings खोल रहा हूँ।"
            }

            lower.contains("display") || lower.contains("screen") && lower.contains("setting") -> {
                action = "display_settings"
                DeviceActions.openApp(context, "settings")
                "Display settings खोल रहा हूँ।"
            }

            lower.contains("apps") && lower.contains("setting") || lower.contains("app info") -> {
                action = "apps_settings"
                DeviceActions.openApp(context, "settings")
                "Apps settings खोल रहा हूँ।"
            }

            lower.contains("storage") || lower.contains("स्टोरेज") -> {
                action = "storage"
                DeviceActions.openApp(context, "settings")
                "Storage settings खोल रहा हूँ।"
            }

            lower.contains("security") && !lower.contains("password") || lower.contains("सुरक्षा") -> {
                action = "security"
                DeviceActions.openApp(context, "settings")
                "Security settings खोल रहा हूँ।"
            }

            lower.contains("language") && lower.contains("setting") || lower.contains("भाषा सेटिंग") -> {
                action = "language_settings"
                DeviceActions.openApp(context, "settings")
                "Language settings खोल रहा हूँ।"
            }

            lower.contains("battery") && lower.contains("setting") || lower.contains("बैटरी सेटिंग") -> {
                action = "battery_settings"
                DeviceActions.openApp(context, "settings")
                "Battery settings खोल रहा हूँ।"
            }

            lower.contains("about phone") || lower.contains("phone info") -> {
                action = "about_phone"
                DeviceActions.openApp(context, "settings")
                "About phone खोल रहा हूँ।"
            }

            lower.startsWith("play ") || lower.contains("चलाओ") || lower.contains("बजाओ") ||
                lower.contains("गाना") || lower.contains("song") || lower.contains("music") -> {
                action = "play_music"
                var q = text.substringAfter("play", "")
                    .substringAfter("चलाओ", "").substringAfter("बजाओ", "")
                    .replace("गाना", "").replace("song", "").replace("music", "").trim()
                if (q.isEmpty()) q = "trending songs"
                DeviceActions.youtubeSearch(context, q)
                "YouTube पर चला रहा हूँ: $q"
            }

            lower.contains("weather") || lower.contains("मौसम") || lower.contains("mausam") -> {
                action = "weather"
                val city = text.replace("weather", "").replace("मौसम", "")
                    .replace("mausam", "").replace("कैसा है", "").trim().ifEmpty { "delhi" }
                DeviceActions.webSearch(context, "$city weather")
                "$city का मौसम दिखा रहा हूँ।"
            }

            mathPattern.containsMatchIn(lower) || lower.contains("plus") || lower.contains("minus") ||
                lower.contains("into ") || lower.contains("multiply") || lower.contains("divide") -> {
                action = "math"
                val answer = evaluateMath(lower)
                if (answer != null) "जवाब है $answer।"
                else "गणना समझ नहीं आई, ऐसे बोलो — 5 into 3।"
            }

            lower.contains("assignment") || lower.contains("notes") || lower.contains("नोट्स") ||
                lower.contains("viva") || lower.contains("exam") || lower.contains("practical") || lower.contains("study plan") -> {
                action = "college_ai"
                com.example.hacker.college.CollegeAI.handle(context, text)
            }

            lower.contains("code ") || lower.contains("coding") || lower.contains("python") || lower.contains("java") ||
                lower.contains("kotlin") || lower.contains("debug") || lower.contains("generate code") || lower.contains("explain code") -> {
                action = "coding_ai"
                com.example.hacker.coding.CodingAI.handle(context, text)
            }

            lower.contains("pdf") || lower.contains("document analysis") || lower.contains("image analysis") || lower.contains("ocr") -> {
                action = "vision_ai"
                com.example.hacker.vision.VisionAI.handle(context, text)
            }

            lower.contains("pair device") || lower.contains("pc par") || lower.contains("device list") || lower.contains("revoke device") -> {
                action = "devices"
                com.example.hacker.devices.DeviceHub.handle(context, text)
            }

            lower.contains("skill") || lower.contains("adaptive") || lower.contains("suggestion") -> {
                action = "skills"
                com.example.hacker.skills.SkillManager.handle(context, text)
            }

            else -> {
                // 1) fuzzy app open
                val fuzzy = fuzzyAppMatch(context, lower)
                if (fuzzy != null) {
                    action = "open_app_fuzzy"
                    com.example.hacker.phonecontrol.AppLauncher.open(context, fuzzy)
                    "$fuzzy खोल रहा हूँ।"
                } else {
                    // 2) HACKER 5.0 goal engine — study/assignment/coding/exam/project plans
                    val goal = com.example.hacker.utils.GoalParser.parse(text)
                    if (goal.intent != com.example.hacker.utils.GoalIntent.UNKNOWN) {
                        action = "goal_plan"
                        val wf = com.example.hacker.utils.WorkflowBuilder.build(goal)
                        val plan = wf.steps.drop(1).joinToString("; ") { it.description }
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            try { com.example.hacker.data.repository.MemoryRepository(context).add("goal", text) } catch (_: Exception) {}
                        }
                        "समझ गया — $plan। पूरा प्लान मेमोरी में सेव कर दिया।"
                    } else {
                        // 3) LLM FALLBACK — AI se analyze karo (agar API key available ho)
                        val llmResponse = try {
                            runBlocking { com.example.hacker.ai.LLMProvider.analyze(context, text) }
                        } catch (_: Exception) { null }

                        if (llmResponse != null) {
                            action = "llm_${llmResponse.action}"
                            // Execute LLM-suggested action
                            executeLLMAction(context, llmResponse.action, llmResponse.params)
                            llmResponse.reply
                        } else {
                            // 4) UNIVERSAL FALLBACK — jo bhi bola, Google par execute karo
                            action = "universal_web"
                            DeviceActions.webSearch(context, text)
                            "'$text' के लिए Google पर ढूँढ रहा हूँ।"
                        }
                    }
                }
            }
        }

        ActivityLogRepository(context).log(text, action, response)
        return ResponseEngine.success(response)
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

    private fun executeLLMAction(context: Context, action: String, params: Map<String, String>) {
        when (action) {
            "torch_on" -> DeviceActions.toggleTorch(context)
            "torch_off" -> DeviceActions.toggleTorch(context)
            "open_whatsapp" -> DeviceActions.openApp(context, "whatsapp")
            "open_instagram" -> DeviceActions.openApp(context, "instagram")
            "open_spotify" -> DeviceActions.openApp(context, "spotify")
            "open_gmail" -> DeviceActions.openApp(context, "gmail")
            "open_chrome" -> DeviceActions.openApp(context, "chrome")
            "open_camera" -> DeviceActions.openCamera(context)
            "play_music" -> {
                val song = params["song"] ?: ""
                if (song.isNotBlank()) DeviceActions.webSearch(context, "$song song")
                else DeviceActions.openApp(context, "music")
            }
            "web_search" -> {
                val query = params["query"] ?: params["q"] ?: ""
                DeviceActions.webSearch(context, query)
            }
            "call_contact" -> {
                val name = params["contact"] ?: params["name"] ?: ""
                DeviceActions.openDialer(context)
            }
            "send_message" -> {
                val contact = params["contact"] ?: ""
                DeviceActions.openApp(context, "whatsapp")
            }
            "set_alarm" -> {
                val time = params["time"] ?: ""
                DeviceActions.openApp(context, "clock")
            }
            "set_timer" -> {
                val minutes = params["minutes"] ?: params["duration"] ?: "5"
                try {
                    TimerController.start(context, minutes.toInt() * 60)
                } catch (_: Exception) {
                    DeviceActions.openApp(context, "clock")
                }
            }
            "volume_up" -> DeviceActions.volumeUp(context)
            "volume_down" -> DeviceActions.volumeDown(context)
            "wifi_settings" -> DeviceActions.openWifi(context)
            "bluetooth_settings" -> DeviceActions.openBluetooth(context)
            "battery_settings" -> DeviceActions.openApp(context, "settings")
            "assignment_help" -> {
                val topic = params["topic"] ?: ""
                // Delegate to CollegeAI
            }
            "code_help" -> {
                val topic = params["topic"] ?: ""
                // Delegate to CodingAI
            }
            else -> DeviceActions.webSearch(context, action)
        }
    }

    private val mathPattern = Regex("\\d+\\s*[+\\-*/x]\\s*\\d+")

    private fun evaluateMath(lower: String): Double? {
        return try {
            val m = Regex("(-?\\d+(?:\\.\\d+)?)\\s*(plus|minus|into|multiplied by|multiply|divided by|divide|[+\\-*/x])\\s*(-?\\d+(?:\\.\\d+)?)").find(lower)
                ?: return null
            val a = m.groupValues[1].toDoubleOrNull() ?: return null
            val b = m.groupValues[3].toDoubleOrNull() ?: return null
            when (m.groupValues[2].trim()) {
                "plus", "+" -> a + b
                "minus", "-" -> a - b
                "into", "multiplied by", "multiply", "*", "x" -> a * b
                "divided by", "divide", "/" -> if (b == 0.0) null else a / b
                else -> null
            }
        } catch (_: Exception) {
            null
        }
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
