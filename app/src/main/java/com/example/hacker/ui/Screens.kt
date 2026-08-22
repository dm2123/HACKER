package com.example.hacker.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.hacker.data.Conversation
import com.example.hacker.phone.DeviceActions
import com.example.hacker.voice.CommandProcessor
import com.example.hacker.voice.Speaker

@Composable
fun rememberSpeaker(): Speaker {
    val ctx = LocalContext.current
    val speaker = remember { Speaker(ctx) }
    DisposableEffect(Unit) { onDispose { speaker.shutdown() } }
    return speaker
}

@Composable
fun rememberPermissionRequester(): (Array<String>) -> Unit {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }
    return { perms -> launcher.launch(perms) }
}

fun greeting(): String {
    val h = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        h < 12 -> "सुप्रभात"
        h < 17 -> "नमस्ते"
        else -> "शुभ संध्या"
    }
}

@Composable
fun HomeScreen(onNavigate: (String)->Unit = {}) {
    val ctx = LocalContext.current
    var torchOn by remember { mutableStateOf(DeviceActions.isTorchOn()) }
    var battery by remember { mutableStateOf(DeviceActions.batteryLevel(ctx)) }

    androidx.compose.foundation.rememberScrollState().let { }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .let { m -> m }
    ) {
        Text(
            text = "▌ HACKER",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 28.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = greeting() + ", Commander",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.BatteryStd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Battery: $battery%  (${DeviceActions.batteryStatus(ctx)})", color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Quick Actions", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickButton(icon = Icons.Filled.FlashOn, label = if (torchOn) "Torch Off" else "Torch On") {
                torchOn = DeviceActions.toggleTorch(ctx)
                battery = DeviceActions.batteryLevel(ctx)
            }
            QuickButton(icon = Icons.Filled.Call, label = "Dialer") {
                DeviceActions.openDialer(ctx)
            }
            QuickButton(icon = Icons.Filled.Sms, label = "SMS") {
                DeviceActions.openSmsApp(ctx)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickButton(icon = Icons.Filled.Wifi, label = "WiFi") {
                DeviceActions.openWifi(ctx)
            }
            QuickButton(icon = Icons.Filled.Bluetooth, label = "BT") {
                DeviceActions.openBluetooth(ctx)
            }
            QuickButton(icon = Icons.Filled.PlayArrow, label = "YouTube") {
                DeviceActions.openApp(ctx, "youtube")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickButton(icon = Icons.Filled.CameraAlt, label = "Camera") {
                DeviceActions.openCamera(ctx)
            }
            QuickButton(icon = Icons.Filled.ContentCopy, label = "Clipboard") {
                val txt = DeviceActions.clipboardText(ctx)
                if (txt.isNotBlank()) {
                    // show toast
                    android.widget.Toast.makeText(ctx, "Clipboard: ${txt.take(30)}", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    android.widget.Toast.makeText(ctx, "Clipboard empty", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            QuickButton(icon = Icons.Filled.CalendarMonth, label = "Calendar") {
                DeviceActions.openCalendar(ctx)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.foundation.layout.Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                    SmallNav(icon = Icons.Filled.History, label = "History", onClick = { onNavigate("history") })
                }
                androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                    SmallNav(icon = Icons.Filled.Psychology, label = "Memory", onClick = { onNavigate("memory") })
                }
                androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                    SmallNav(icon = Icons.Filled.Build, label = "Tools", onClick = { onNavigate("tools_extra") })
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                    SmallNav(icon = Icons.Filled.Notifications, label = "Notifs", onClick = { onNavigate("notifications") })
                }
                androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                    SmallNav(icon = Icons.Filled.Security, label = "Security", onClick = { onNavigate("security") })
                }
                androidx.compose.foundation.layout.Box(Modifier.weight(1f)) {
                    SmallNav(icon = Icons.Filled.PrivacyTip, label = "Privacy", onClick = { onNavigate("privacy") })
                }
            }
        }
    }
}

@Composable
private fun SmallNav(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: ()->Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun QuickButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.height(64.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun ChatScreen() {
    val ctx = LocalContext.current
    val speaker = rememberSpeaker()
    var input by remember { mutableStateOf("") }
    val requester = rememberPermissionRequester()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            reverseLayout = true
        ) {
            items(Conversation.messages.reversed()) { msg ->
                ChatBubble(msg.text, msg.isUser, msg.time)
                Spacer(modifier = Modifier.height(6.dp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type command...") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                if (input.isBlank()) return@IconButton
                val cmd = input.trim()
                input = ""
                Conversation.add(cmd, true)
                val reply = CommandProcessor.handle(ctx, cmd)
                Conversation.add(reply, false)
                speaker.speak(reply)
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text, color = MaterialTheme.colorScheme.onSurface)
                Text(time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun VoiceScreen() {
    val ctx = LocalContext.current
    val speaker = rememberSpeaker()
    val requester = rememberPermissionRequester()
    var heard by remember { mutableStateOf("") }
    var reply by remember { mutableStateOf("Tap mic and speak in Hindi or English") }

    val stt = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        val list = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val text = list?.firstOrNull()
        if (!text.isNullOrBlank()) {
            heard = text
            val r = CommandProcessor.handle(ctx, text)
            reply = r
            Conversation.add(text, true)
            Conversation.add(r, false)
            speaker.speak(r)
        } else {
            reply = "कुछ सुनाई नहीं दिया, फिर कोशिश करो।"
        }
    }

    fun listen() {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requester(arrayOf(Manifest.permission.RECORD_AUDIO))
            reply = "Mic permission चाहिए। Settings से दो।"
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "HACKER सुन रहा है...")
        }
        try {
            stt.launch(intent)
        } catch (e: Exception) {
            reply = "Speech recognition उपलब्ध नहीं है इस डिवाइस पर।"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = reply,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            modifier = Modifier.padding(24.dp)
        )
        if (heard.isNotEmpty()) {
            Text("तुमने कहा: $heard", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        FloatingActionButton(
            onClick = { listen() },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(88.dp)
        ) {
            Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = Color.Black, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Tap to speak", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


@Composable
private fun SettingsNav(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: ()->Unit) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) { Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp); Text(desc, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp) }
            Icon(Icons.Filled.PlayArrow, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PermissionRow(label: String, granted: Boolean, onRequest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = if (granted) MaterialTheme.colorScheme.primary else Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
            if (!granted) {
                OutlinedButton(onClick = onRequest) { Text("Grant") }
            } else {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

fun requestAssistantRole(context: Context) {
    // Preferred path: Android 10+ RoleManager dialog (MUST use startActivityForResult)
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val rm = context.getSystemService(Context.ROLE_SERVICE) as? android.app.role.RoleManager
            if (rm != null && rm.isRoleAvailable(android.app.role.RoleManager.ROLE_ASSISTANT)) {
                if (!rm.isRoleHeld(android.app.role.RoleManager.ROLE_ASSISTANT)) {
                    val intent = rm.createRequestRoleIntent(android.app.role.RoleManager.ROLE_ASSISTANT)
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        activity.startActivityForResult(intent, 2001)
                    } else {
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                    return
                } else {
                    // Already held but user tapped again — show settings for verification
                    openAssistantSettings(context)
                    return
                }
            }
        }
    } catch (_: Exception) {
        // fall through to settings
    }
    openAssistantSettings(context)
}

fun openAssistantSettings(context: Context) {
    // Try multiple OEM-compatible settings screens
    val candidates = listOf(
        android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS,
        "android.settings.MANAGE_DEFAULT_APPS_SETTINGS",
        android.provider.Settings.ACTION_SETTINGS
    )
    for (action in candidates) {
        try {
            val i = Intent(action)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
            return
        } catch (_: Exception) {}
    }
}

fun isHackerEligible(context: Context): Boolean {
    return try {
        val info = context.packageManager.getServiceInfo(
            android.content.ComponentName(context, com.example.hacker.services.HackerVoiceInteractionService::class.java),
            PackageManager.GET_META_DATA
        )
        info.enabled && info.metaData?.getInt("android.voice_interaction") != 0
    } catch (_: Exception) {
        try {
            val info = context.packageManager.getServiceInfo(
                android.content.ComponentName(context.packageName, "com.example.hacker.services.HackerVoiceInteractionService"),
                0
            )
            info.enabled
        } catch (_: Exception) { false }
    }
}
