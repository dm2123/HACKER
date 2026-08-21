package com.example.hacker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    var isMicrophoneEnabled by remember { mutableStateOf(true) }
    var isVoiceProfileEnabled by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Microphone toggle
            androidx.compose.material3.SettingsCard(
                title = "Microphone",
                description = "Voice input is currently ON"
            ) {
                androidx.compose.material3.Switch(
                    checked = isMicrophoneEnabled,
                    onCheckedChange = { isMicrophoneEnabled = it }
                )
            }

            // Voice profile
            androidx.compose.material3.SettingsCard(
                title = "Voice Profile",
                description = "Voice profile ACTIVE"
            ) {
                androidx.compose.material3.Switch(
                    checked = isVoiceProfileEnabled,
                    onCheckedChange = { isVoiceProfileEnabled = it }
                )
            }

            // Notifications
            androidx.compose.material3.SettingsCard(
                title = "Notifications",
                description = "Notification access is currently ON"
            ) {
                androidx.compose.material3.Switch(
                    checked = isNotificationsEnabled,
                    onCheckedChange = { isNotificationsEnabled = it }
                )
            }

            // Memory
            androidx.compose.material3.SettingsCard(
                title = "Memory",
                description = "Memory storage is currently ON"
            ) {
                androidx.compose.material3.Switch(
                    checked = isNotificationsEnabled, // Using notifications var as placeholder
                    onCheckedChange = {}
                )
            }
        }
    }
}

@Composable
fun SettingsCard(title: String, description: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        content()
    }
}