package com.example.hacker.features.home

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
fun HomeScreen() {
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
                text = "HACKER",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Ready to Help",
                style = MaterialTheme.typography.bodyMedium
            )

            // Microphone FAB
            androidx.compose.material3.FloatingActionButton(
                onClick = { /* TODO: Start voice interaction */ },
                modifier = Modifier.padding(8.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material3.icons.Default.Mic,
                    contentDescription = "Speak to HACKER"
                )
            }
        }
    }
}