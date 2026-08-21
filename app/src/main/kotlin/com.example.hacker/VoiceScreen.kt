package com.example.hacker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VoiceScreen() {
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
                text = "🎙️ HACKER Voice",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Say 'Hey HACKER' to activate",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Wake phrase input
            androidx.compose.material3.TextField(
                value = "",
                onValueChange = { },
                label = { Text("Wake phrase") },
                modifier = Modifier.fillMaxWidth()
            )

            // Record button
            androidx.compose.material3.FloatingActionButton(
                onClick = { /* TODO: Start listening */ },
                modifier = Modifier.size(64.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material3.icons.Default.Mic,
                    contentDescription = "Record command"
                )
            }
        }
    }
}