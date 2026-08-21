package com.example.hacker.features.security

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SecurityScreen() {
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
                text = "🔒 Security",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Security controls and settings",
                style = MaterialTheme.typography.bodyMedium
            )

            // Security action cards
            androidx.compose.material3.Button(
                onClick = { /* TODO: Permission Manager */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Permission Manager")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Lock Screen Settings */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Lock Screen Settings")
            }

            androidx.compose.material3.Button(
                onClick = { /* TODO: Activity Logs */ },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Activity Logs")
            }
        }
    }
}