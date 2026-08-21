package com.example.hacker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HackerApp() {
    var screen by remember { mutableStateOf("home") }

    MaterialTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = screen == "home",
                        onClick = { screen = "home" },
                        icon = { Text("🏠") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = screen == "chat",
                        onClick = { screen = "chat" },
                        icon = { Text("💬") },
                        label = { Text("Chat") }
                    )
                    NavigationBarItem(
                        selected = screen == "voice",
                        onClick = { screen = "voice" },
                        icon = { Text("🎙️") },
                        label = { Text("Voice") }
                    )
                    NavigationBarItem(
                        selected = screen == "settings",
                        onClick = { screen = "settings" },
                        icon = { Text("⚙️") },
                        label = { Text("Settings") }
                    )
                }
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (screen) {
                    "home" -> HomeContent()
                    "chat" -> ChatContent()
                    "voice" -> VoiceContent()
                    "settings" -> SettingsContent()
                }
            }
        }
    }
}

@Composable
fun HomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "HACKER", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Ready to Help",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        FloatingActionButton(onClick = { }) {
            Text("🎙️")
        }
        Text(
            text = "\"Hey HACKER...\"",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun ChatContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chat", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "AI chat coming soon",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun VoiceContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Voice", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "Say \"Hey HACKER\" to activate",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
fun SettingsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { }) {
                Text("Microphone: ON")
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { }) {
                Text("Memory: ON")
            }
        }
    }
}