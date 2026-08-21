package com.example.hacker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.bottomNavigationBar
import androidx.compose.material3.bottomNavigationItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.icons.Default.Mic
import androidx.compose.material3.icons.Default.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.NavigationBar
import androidx.compose.ui.NavigationBarItem
import androidx.compose.ui.Unit
import androidx.compose.ui.layout.Column
import androidx.compose.ui.layout.Row
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.textOf
import androidx.compose.ui.text.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.syntax.layoutKt.text
import androidx.compose.ui.syntax.textKt.text
import androidx.compose.ui.syntax.textKt.textField
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardShortcut
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.basicTextField
import androidx.compose.ui.text.input.submitKey
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.route.route
import androidx.activity.compose.navController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackerTheme {
                setContent {
                    HackerApplication()
                }
            }
        }
    }
}

@Composable
fun HackerApplication() {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                parameterStringConverter = { params -> params }
            ) {
                composable("home") {
                    HackerHomeScreen(navController)
                }
                composable("chat") {
                    ChatScreen()
                }
                composable("voice") {
                    VoiceScreen()
                }
                composable("settings") {
                    SettingsScreen()
                }
            }

            // Bottom navigation bar
            androidx.compose.material3.BottomNavigationBar(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.ui.unit.dp(4.dp),
                elevation = 8.dp
            ) {
                bottomNavigationItem(
                    icon = { Text("🏠") },
                    label = { Text("Home") },
                    selected = navController.currentDestination?.route == "home",
                    onClick = { navController.navigate("home") }
                )
                bottomNavigationItem(
                    icon = { Text("💬") },
                    label = { Text("Chat") },
                    selected = navController.currentDestination?.route == "chat",
                    onClick = { navController.navigate("chat") }
                )
                bottomNavigationItem(
                    icon = { Text("🎙️") },
                    label = { Text("Voice") },
                    selected = navController.currentDestination?.route == "voice",
                    onClick = { navController.navigate("voice") }
                )
                bottomNavigationItem(
                    icon = { Text("⚙️") },
                    label = { Text("Settings") },
                    selected = navController.currentDestination?.route == "settings",
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    }
}

@Composable
fun HackerHomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
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

        FloatingActionButton(
            onClick = { /* TODO: Start voice interaction */ },
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Mic,
                contentDescription = "Speak to HACKER"
            )
        }
    }
}

@Composable
fun bottomNavigationItem(
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.BottomNavigationItem(
        icon = { icon },
        label = { label },
        selected = selected,
        onClick = onClick
    )
}