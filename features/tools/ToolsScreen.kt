package com.example.hacker.features.tools

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolsScreen() {
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
                text = "🛠️ Tools",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Available tools and utilities",
                style = MaterialTheme.typography.bodyMedium
            )

            // Tools grid
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
            ) {
                // App Launcher tool
                androidx.compose.material3.Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(onClick = { /* TODO: App Launcher */ })
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material3.icons.Default.AppLoader,
                        contentDescription = "App Launcher"
                    )
                    Text("App Launcher")
                }

                // Torch tool
                androidx.compose.material3.Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(onClick = { /* TODO: Torch */ })
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material3.icons.Default.LightMode,
                        contentDescription = "Torch"
                    )
                    Text("Torch")
                }

                // Volume tool
                androidx.compose.material3.Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(onClick = { /* TODO: Volume */ })
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material3.icons.Default.VolumeUp,
                        contentDescription = "Volume"
                    )
                    Text("Volume")
                }
            }
        }
    }
}