package com.example.hacker.features.voice

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VoiceInteractionScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(
                text = "🎤 Voice",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Voice interaction interface",
                style = MaterialTheme.typography.bodyMedium
            )

            // Voice status
            androidx.compose.material3.Chip(
                label = Text("Listening: OFF"),
                modifier = Modifier.padding(4.dp)
            )

            // Wake phrase
            androidx.compose.material3.TextField(
                value = "",
                onValueChange = { },
                label = { Text("Wake phrase") },
                modifier = Modifier.fillMaxWidth()
            )

            // Voice commands list
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                Text("Recent commands:", style = MaterialTheme.typography.bodySmall)
                // TODO: Show recent voice commands
            }
        }
    }
}