package com.example.hacker.features.privacy

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyScreen() {
    var isMicrophoneOn by remember { mutableStateOf(true) }
    var isVoiceProfileActive by remember { mutableStateOf(true) }
    var isNotificationsSharing by remember { mutableStateOf(true) }
    var isCloudAI by remember { mutableStateOf(true) }
    var isMemory by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Microphone
            androidx.compose.material3.SettingsCard(
                title = "Microphone",
                description = "Voice input is currently ${if (isMicrophoneOn) "ON" else "OFF"}"
            ) {
                androidx.compose.material3.Switch(
                    checked = isMicrophoneOn,
                    onCheckedChange = { isMicrophoneOn = it }
                )
            }

            // Voice profile
            androidx.compose.material3.SettingsCard(
                title = "Voice Profile",
                description = "Voice verification ${if (isVoiceProfileActive) "ACTIVE" else "INACTIVE"}"
            ) {
                androidx.compose.material3.Switch(
                    checked = isVoiceProfileActive,
                    onCheckedChange = { isVoiceProfileActive = it }
                )
            }

            // Notifications
            androidx.compose.material3.SettingsCard(
                title = "Notifications",
                description = "Notification access ${if (isNotificationsSharing) "ON" else "OFF"}"
            ) {
                androidx.compose.material3.Switch(
                    checked = isNotificationsSharing,
                    onCheckedChange = { isNotificationsSharing = it }
                )
            }

            // Cloud AI
            androidx.compose.material3.SettingsCard(
                title = "Cloud AI",
                description = "Cloud-based AI processing ${if (isCloudAI) "ON" else "OFF"}"
            ) {
                androidx.compose.material3.Switch(
                    checked = isCloudAI,
                    onCheckedChange = { isCloudAI = it }
                )
            }

            // Memory
            androidx.compose.material3.SettingsCard(
                title = "Memory",
                description = "Personal memory ${if (isMemory) "ON" else "OFF"}"
            ) {
                androidx.compose.material3.Switch(
                    checked = isMemory,
                    onCheckedChange = { isMemory = it }
                )
            }
        }
    }
}