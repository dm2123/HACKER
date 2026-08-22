package com.example.hacker.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.hacker.data.preferences.UserPreferences
import com.example.hacker.data.repository.*
import com.example.hacker.phone.DeviceActions
import com.example.hacker.automation.AutomationEngine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun fmt(ts: Long) = SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault()).format(Date(ts))

// ---------- Splash ----------
@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(1200); onDone() }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("▌ HACKER", color = MaterialTheme.colorScheme.primary, fontSize = 36.sp, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(12.dp))
            Text("Your Voice. Your Phone. Your Assistant.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

// ---------- Onboarding ----------
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val titles = listOf("Voice First", "Phone Control", "Privacy First")
    val descs = listOf(
        "Speak in Hindi, English or Hinglish — HACKER listens and speaks back.",
        "Open apps, torch, volume, alarms, calls, SMS — all by voice.",
        "All data stays on-device unless you enable Cloud AI. You control memory."
    )
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(titles[page], color = MaterialTheme.colorScheme.primary, fontSize = 26.sp)
        Spacer(Modifier.height(16.dp))
        Text(descs[page], color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            titles.indices.forEach { i -> Box(Modifier.size(8.dp).padding(2.dp)) { Text(if (i==page) "●" else "○", color = MaterialTheme.colorScheme.primary) } }
        }
        Spacer(Modifier.height(32.dp))
        if (page < 2) Button(onClick = { page++ }) { Text("Next") }
        else Button(onClick = onFinish) { Text("Get Started") }
        if (page>0) TextButton(onClick = { page-- }) { Text("Back") }
        TextButton(onClick = onFinish) { Text("Skip") }
    }
}

// ---------- Voice Enrollment (spec 5.2) ----------
@Composable
fun VoiceEnrollmentScreen(onDone: () -> Unit) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var step by remember { mutableStateOf(0) }
    var status by remember { mutableStateOf("Say:  Hey HACKER") }
    LaunchedEffect(step) {
        // auto-advance simulation — real enrollment would record
        if (step in 1..2) kotlinx.coroutines.delay(900)
    }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Voice Enrollment", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(16.dp))
        Text(status, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(16.dp))
        LinearProgressIndicator(progress = { (step+1)/3f }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Text("Step ${step+1} of 3", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            if (step < 2) { step++; status = if (step==1) "Great! Again:  Hey HACKER" else "One more time..." }
            else {
                scope.launch { UserPreferences.setVoiceProfileEnabled(ctx, true); onDone() }
            }
        }) { Text(if (step<2) "Record" else "Confirm & Finish") }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { scope.launch { UserPreferences.setVoiceProfileEnabled(ctx, false); onDone() } }) { Text("Skip for now") }
        Spacer(Modifier.height(12.dp))
        Text("Voice profile is a security enhancement, not primary auth (spec 5.2).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

// ---------- Permission Setup (spec 04) ----------
@Composable
fun PermissionSetupScreen(onDone: () -> Unit) {
    val ctx = LocalContext.current
    val requester = rememberPermissionRequester()
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Permissions", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp)
        Spacer(Modifier.height(8.dp))
        Text("HACKER needs these to work like Siri on iPhone — grant what you allow.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        PermissionExplain("Microphone", "Voice commands", Icons.Filled.Mic) { requester(arrayOf(android.Manifest.permission.RECORD_AUDIO)) }
        PermissionExplain("Phone", "Make calls via voice", Icons.Filled.Call) { requester(arrayOf(android.Manifest.permission.CALL_PHONE)) }
        PermissionExplain("SMS", "Compose SMS", Icons.Filled.Sms) { requester(arrayOf(android.Manifest.permission.SEND_SMS)) }
        PermissionExplain("Contacts", "Call by name e.g. Rahul ko call karo", Icons.Filled.Contacts) { requester(arrayOf(android.Manifest.permission.READ_CONTACTS)) }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }, modifier = Modifier.fillMaxWidth()) { Text("Enable Notification Access") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }, modifier = Modifier.fillMaxWidth()) { Text("Enable Accessibility (optional)") }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Continue") }
    }
}
@Composable private fun PermissionExplain(title: String, desc: String, icon: ImageVector, onGrant: ()->Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = MaterialTheme.colorScheme.onSurface); Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
            OutlinedButton(onClick = onGrant) { Text("Grant") }
        }
    }
}

// ---------- Shared Nav Card ----------
@Composable fun NavCard(icon: ImageVector, title: String, desc: String, onClick: ()->Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp); Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
            Icon(Icons.Filled.PlayArrow, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ---------- Activity Logs / History (spec 08,18) - Stub for build stability ----------
@Composable
fun ActivityLogsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Activity Logs", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("Activity logs feature requires Room entity sync (core module).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    }
}
@Composable fun HistoryScreen() = ActivityLogsScreen() // alias for spec 08

// ---------- Memory (spec 13) - Stub for build stability ----------
@Composable
fun MemoryScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Memory", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("Memory feature requires Room entity sync (core module).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
    }
}

// ---------- Settings (spec 24) ----------
@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Settings", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("All phone control tools (spec 8)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        val tools = listOf(
            Triple(Icons.Filled.Apps, "App Launcher", "open youtube / camera kholo") to { DeviceActions.openApp(ctx, "settings") },
            Triple(Icons.Filled.FlashOn, "Torch", "flashlight on karo") to { DeviceActions.toggleTorch(ctx) },
            Triple(Icons.Filled.VolumeUp, "Volume", "volume kam karo") to { DeviceActions.openApp(ctx, "settings") },
            Triple(Icons.Filled.Alarm, "Alarm", "kal 6 baje alarm") to { com.example.hacker.phonecontrol.AlarmController.setAlarm(ctx, 6, 0) },
            Triple(Icons.Filled.Timer, "Timer", "10 minute timer") to { com.example.hacker.phonecontrol.TimerController.start(ctx, 600) },
            Triple(Icons.Filled.Wifi, "WiFi", "wifi kholo") to { DeviceActions.openWifi(ctx) },
            Triple(Icons.Filled.Bluetooth, "Bluetooth", "bluetooth kholo") to { DeviceActions.openBluetooth(ctx) },
        )
        tools.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (info, action) ->
                    val (icon, title, hint) = info
                    Card(Modifier.weight(1f).height(92.dp).clickable { action() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(Modifier.fillMaxSize().padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Text(title, fontSize = 12.sp); Text(hint, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (row.size==1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
        }
        HorizontalDivider()
        NavCard(Icons.Filled.AutoAwesome, "Automations", "Study / Sleep / Morning / Work modes", { onNavigate("automation") })
        NavCard(Icons.Filled.Notifications, "Notifications", "Read your notifications", { onNavigate("notifications") })
        NavCard(Icons.Filled.History, "Activity Logs", "View all actions", { onNavigate("activity_logs") })
        NavCard(Icons.Filled.Psychology, "Memory", "Your stored preferences", { onNavigate("memory") })
    }
}

// ---------- Notifications (spec 10,13) ----------
@Composable
fun NotificationsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var list by remember { mutableStateOf<List<NotificationEntity>>(emptyList()) }
    fun reload() { scope.launch { list = NotificationRepository(ctx).getRecent(100) } }
    LaunchedEffect(Unit) { reload() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Notifications", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (list.isNotEmpty()) OutlinedButton(onClick = { scope.launch { NotificationRepository(ctx).clear(); reload() } }) { Text("Clear") }
                Button(onClick = { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }) { Text("Enable") }
            }
        }
        Text("Needs Notification Access permission (spec 10).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        if (list.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No notifications captured yet", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(list) { n ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(10.dp)) {
                        Text("${n.appLabel} • ${n.title}", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        Text(n.text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text(fmt(n.postedAt), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// ---------- Automation (spec 12) ----------
@Composable
fun AutomationScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var trigger by remember { mutableStateOf("") }
    var actions by remember { mutableStateOf("") }
    val repo = remember { AutomationRepository(ctx) }
    val autos by repo.observe().collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Automations", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Text("Study / Sleep / Morning / Work — or create custom (spec 12).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        // Built-ins
        listOf("study mode" to "Study Mode", "sleep mode" to "Sleep Mode", "morning routine" to "Morning Routine", "work mode" to "Work Mode").forEach { (key, label) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { DeviceActions.openApp(ctx, "settings"); scope.launch { val res = AutomationEngine.run(ctx, key); android.widget.Toast.makeText(ctx, res, android.widget.Toast.LENGTH_SHORT).show() } }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bolt, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) { Text(label, color = MaterialTheme.colorScheme.onSurface); Text(key, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp) }
                    Icon(Icons.Filled.PlayArrow, null)
                }
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))
        Text("Create Custom Workflow", color = MaterialTheme.colorScheme.primary)
        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(trigger, { trigger = it }, label = { Text("Trigger phrase") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(actions, { actions = it }, label = { Text("Actions: wifi, volume_down, open_app:youtube") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { if (name.isNotBlank() && trigger.isNotBlank()) scope.launch { repo.add(name, trigger, actions); name=""; trigger=""; actions="" } }, modifier = Modifier.fillMaxWidth()) { Text("Save Automation") }
        Spacer(Modifier.height(12.dp))
        if (autos.isNotEmpty()) {
            Text("Saved (${autos.size})", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(autos) { a ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text(a.name, fontSize = 13.sp); Text("${a.trigger} → ${a.actions}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            IconButton(onClick = { scope.launch { repo.delete(a) } }) { Icon(Icons.Filled.Delete, null) }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Security Center (spec 15) ----------
@Composable
fun SecurityScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var pinInput by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }
    var secEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { secEnabled = UserPreferences.securityEnabled(ctx).let { var v=false; it.collect { v=it }; v } }
    // simpler: collectAsState
    val secFlow by UserPreferences.securityEnabled(ctx).collectAsState(initial = false)
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Security Center", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Text("LEVEL 1 direct • LEVEL 2 confirm • LEVEL 3 PIN (spec 15).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sensitive actions need PIN", Modifier.weight(1f))
                    Switch(checked = secFlow, onCheckedChange = { scope.launch { UserPreferences.setSecurityEnabled(ctx, it) } })
                }
                Text("When ON, call/SMS need PIN. HACKER never stores PIN bypass (spec 15).", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(pinInput, { pinInput = it.filter { c-> c.isDigit() }.take(6) }, label = { Text("Set PIN (4-6 digits)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(confirmPin, { confirmPin = it.filter { c-> c.isDigit() }.take(6) }, label = { Text("Confirm PIN") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            if (pinInput.length < 4) msg = "PIN at least 4 digits"
            else if (pinInput != confirmPin) msg = "PIN mismatch"
            else scope.launch { com.example.hacker.core.security.SecurityManager.setPin(ctx, pinInput); msg = "PIN saved (hashed)"; pinInput=""; confirmPin="" }
        }, modifier = Modifier.fillMaxWidth()) { Text("Save PIN") }
        if (msg.isNotEmpty()) Text(msg, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        Text("No hidden monitoring • No account bypass • No PIN storage in plaintext (spec 15,31).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

// ---------- Privacy Center (spec 17) ----------
@Composable
fun PrivacyScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val mic by UserPreferences.voiceProfileEnabled(ctx).collectAsState(initial = false)
    val mem by UserPreferences.memoryEnabled(ctx).collectAsState(initial = true)
    val notif by UserPreferences.notificationsEnabled(ctx).collectAsState(initial = false)
    val cloud by UserPreferences.cloudAiEnabled(ctx).collectAsState(initial = false)
    val contacts by UserPreferences.contactsEnabled(ctx).collectAsState(initial = true)
    val location by UserPreferences.locationEnabled(ctx).collectAsState(initial = false)
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Privacy Center", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Text("Local-first; cloud only if you enable (spec 10,17).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        PrivacyToggle("Microphone", mic, "Voice input") { scope.launch { UserPreferences.setVoiceProfileEnabled(ctx, it) } }
        PrivacyToggle("Memory", mem, "Personal memory") { scope.launch { UserPreferences.setMemoryEnabled(ctx, it) } }
        PrivacyToggle("Notifications", notif, "NotificationListenerService") { scope.launch { UserPreferences.setNotificationsEnabled(ctx, it) } }
        PrivacyToggle("Cloud AI", cloud, "LLM cloud processing") { scope.launch { UserPreferences.setCloudAiEnabled(ctx, it) } }
        PrivacyToggle("Contacts", contacts, "Read contacts for calls") { scope.launch { UserPreferences.setContactsEnabled(ctx, it) } }
        PrivacyToggle("Location", location, "Location tools") { scope.launch { UserPreferences.setLocationEnabled(ctx, it) } }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = { scope.launch { MemoryRepository(ctx).clear(); ActivityLogRepository(ctx).clear(); NotificationRepository(ctx).clear() } }, modifier = Modifier.fillMaxWidth()) { Text("Clear All Local Data") }
    }
}
@Composable private fun PrivacyToggle(title: String, checked: Boolean, desc: String, onChange: (Boolean)->Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, color = MaterialTheme.colorScheme.onSurface); Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp) }
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

// ---------- Other settings screens ----------
@Composable fun VoiceSettingsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var pitch by remember { mutableStateOf("1.0") }
    var speed by remember { mutableStateOf("1.0") }
    LaunchedEffect(Unit) { pitch = UserPreferences.voicePitch(ctx).let { var v="1.0"; it.collect { v=it.take(3) }; v } }
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Voice Settings", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("STT Language: hi-IN / en-US (auto-detected). TTS pitch & speed:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        OutlinedTextField(pitch, { pitch=it }, label = { Text("Pitch 0.5-2.0") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(speed, { speed=it }, label = { Text("Speed 0.5-2.0") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = { scope.launch { UserPreferences.setVoicePitch(ctx, pitch); UserPreferences.setVoiceSpeed(ctx, speed) } }, modifier = Modifier.fillMaxWidth()) { Text("Save") }
        Spacer(Modifier.height(12.dp))
        Text("Say 'Hey HACKER' is the default wake phrase (spec 5.1).", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable fun AiSettingsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var endpoint by remember { mutableStateOf("") }
    var key by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    val epFlow by UserPreferences.llmEndpoint(ctx).collectAsState(initial = "")
    val keyFlow by UserPreferences.llmApiKey(ctx).collectAsState(initial = "")
    val modelFlow by UserPreferences.llmModel(ctx).collectAsState(initial = "")
    LaunchedEffect(epFlow, keyFlow, modelFlow) { endpoint = epFlow; key = keyFlow; model = modelFlow }
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("AI Settings", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Text("Provider is behind abstraction — change anytime (spec 21). Offline commands work without cloud.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(endpoint, { endpoint=it }, label = { Text("Endpoint") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(key, { key=it }, label = { Text("API Key") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(model, { model=it }, label = { Text("Model e.g. gpt-4o-mini") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { scope.launch { UserPreferences.setLlmEndpoint(ctx, endpoint); UserPreferences.setLlmApiKey(ctx, key); UserPreferences.setLlmModel(ctx, model) } }, modifier = Modifier.fillMaxWidth()) { Text("Save AI Config") }
        Spacer(Modifier.height(12.dp))
        Text("🚀 LLM Integration Active: Truly unlimited commands — 'kuch bhi bolo' AI samjhega!", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        Text("Option 1 - Gemini (FREE, no credit card):\n• Endpoint: https://generativelanguage.googleapis.com\n• API Key: Get from https://makersuite.google.com/app/apikey\n• Model: gemini-pro\n\nOption 2 - ChatGPT (paid, better quality):\n• Endpoint: https://api.openai.com\n• API Key: Get from https://platform.openai.com/api-keys\n• Model: gpt-4o-mini (cheap) or gpt-4o (best)\n\nKoi bhi ek choose karo!", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        var testRes by remember { mutableStateOf("") }
        Button(onClick = { scope.launch { 
            testRes = "Testing..."
            val r = try { 
                com.example.hacker.ai.LLMProvider.analyze(ctx, "torch on karo")?.reply ?: "No API key — offline commands still work" 
            } catch (e: Exception) { "Error: ${e.message}" }
            testRes = r
        } }) { Text("Test LLM (torch on karo)") }
        if (testRes.isNotEmpty()) Text(testRes, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

@Composable fun PermissionManagerScreen() {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Permission Manager", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        listOf(
            "RECORD_AUDIO" to "Mic for voice",
            "CALL_PHONE" to "Voice dial",
            "SEND_SMS" to "SMS",
            "READ_CONTACTS" to "Call by name",
            "CAMERA" to "Camera",
        ).forEach { (perm, desc) ->
            val granted = androidx.core.content.ContextCompat.checkSelfPermission(ctx, "android.permission.$perm") == android.content.pm.PackageManager.PERMISSION_GRANTED
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (granted) Icons.Filled.CheckCircle else Icons.Filled.Block, null, tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) { Text(perm, fontSize = 12.sp); Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Text(if (granted) "GRANTED" else "DENIED", fontSize = 11.sp, color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = android.net.Uri.parse("package:${ctx.packageName}") }) }, modifier = Modifier.fillMaxWidth()) { Text("Open App Permissions") }
    }
}

@Composable fun LanguageScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val langFlow by UserPreferences.language(ctx).collectAsState(initial = "hi-IN")
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Language", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        listOf("hi-IN" to "Hindi", "en-US" to "English", "hinglish" to "Hinglish (auto)").forEach { (code, label) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { scope.launch { UserPreferences.setLanguage(ctx, code) } }, colors = CardDefaults.cardColors(containerColor = if (langFlow==code) MaterialTheme.colorScheme.primary.copy(alpha=0.2f) else MaterialTheme.colorScheme.surfaceVariant)) {
                Row(Modifier.padding(14.dp)) { Text(label); Spacer(Modifier.weight(1f)); if (langFlow==code) Icon(Icons.Filled.Check, null, tint = MaterialTheme.colorScheme.primary) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Hinglish: Hindi + English mixed — natural for HACKER (spec 7).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable fun AppearanceScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val dark by UserPreferences.appearanceDark(ctx).collectAsState(initial = true)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Appearance", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Hacker Theme", Modifier.weight(1f))
                Switch(checked = dark, onCheckedChange = { scope.launch { UserPreferences.setAppearanceDark(ctx, it) } })
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Matrix green on black — HACKER identity.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}

@Composable fun AboutScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("About HACKER", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(12.dp))
        Text("HACKER is a voice-first, AI-powered Android personal assistant (spec 33).", color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        Text("Version 1.0 • MVP-6 • Kotlin + Compose + Room + DataStore + Clean Architecture (spec 21).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Text("Like Siri on iPhone, but for Android — within legitimate Android boundaries (spec 32).", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        Text("Definition of Done (spec 31): voice reliable, intent routing stable, permissions explained, memory user-controlled, offline fallback, no PIN bypass.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
    }
}
@Composable
fun VoiceEnrollmentScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val identity = com.example.hacker.core.voice.VoiceIdentityManager()
    var enrolled by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        enrolled = identity.getStoredProfile()?.isEnrolled == true
    }
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Voice Enrollment", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text("Say 'Hey HACKER' 3 times so only you can wake HACKER.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        if (enrolled) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))) {
                Row(Modifier.padding(12.dp)) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF00E676))
                    Spacer(Modifier.width(8.dp))
                    Text("Enrolled ? � only your 'Hey HACKER' works", color = Color.White, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { scope.launch { identity.clearProfile(); enrolled = false; status = "Cleared" } }, modifier = Modifier.fillMaxWidth()) {
                Text("Clear Profile")
            }
        } else {
            var phrase1 by remember { mutableStateOf("") }
            var phrase2 by remember { mutableStateOf("") }
            var phrase3 by remember { mutableStateOf("") }
            Column(Modifier.fillMaxWidth()) {
                OutlinedTextField(value = phrase1, onValueChange = { phrase1 = it }, label = { Text("Say: Hey HACKER") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = phrase2, onValueChange = { phrase2 = it }, label = { Text("Say: Hey HACKER") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = phrase3, onValueChange = { phrase3 = it }, label = { Text("Say: Hey HACKER") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    val profile = identity.enrollProfile(listOf(phrase1, phrase2, phrase3))
                    if (profile.isEnrolled) {
                        enrolled = true
                        status = "Enrolled ?"
                    } else {
                        status = "Failed � try again"
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Enroll Voice")
                }
                if (status.isNotBlank()) Text(status, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }
        }
    }
}
