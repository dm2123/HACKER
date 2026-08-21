package com.example.hacker.features.memory

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MemoryScreen() {
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
                text = "🧠 Memory",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Your memories are stored locally",
                style = MaterialTheme.typography.bodyMedium
            )

            // Memory controls
            androidx.compose.material3.Button(
                onClick = { /* TODO: View Memory */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("View Memory")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Edit Memory */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Edit Memory")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Delete Memory */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Delete Memory")
            }
        }
    }
}