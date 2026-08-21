package com.example.hacker.features.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsMainScreen() {
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
                text = "⚙️ Settings",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Configure HACKER settings",
                style = MaterialTheme.typography.bodyMedium
            )

            // Settings categories
            androidx.compose.material3.Button(
                onClick = { /* TODO: Open Appearance */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Appearance")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Open Language */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Language")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Open AI Settings */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("AI Settings")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Open Permission Manager */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Permission Manager")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Open Security Center */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Security Center")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Open Privacy Center */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Privacy Center")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: View Activity Logs */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Activity Logs")
            }
        }
    }
}