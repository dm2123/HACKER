package com.example.hacker.features.history

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConversationHistoryScreen() {
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
                text = "📜 History",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Conversation history",
                style = MaterialTheme.typography.bodyMedium
            )

            // Placeholder for conversations
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Text("No conversations yet", style = MaterialTheme.typography.bodySmall)
            }

            // Controls
            androidx.compose.material3.Button(
                onClick = { /* TODO: Clear all history */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Clear All")
            }
        }
    }
}