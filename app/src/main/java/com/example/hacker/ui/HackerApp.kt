package com.example.hacker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hacker.data.preferences.UserPreferences
import kotlinx.coroutines.launch

enum class Tab(val label: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Filled.Home, "home"),
    Chat("Chat", Icons.AutoMirrored.Filled.Chat, "chat"),
    Voice("Voice", Icons.Filled.Mic, "voice"),
    Settings("Settings", Icons.Filled.Settings, "settings")
}

@Composable
fun HackerApp() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val nav = rememberNavController()
    var onboardingDone by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(Unit) {
        UserPreferences.onboardingDone(ctx).collect { onboardingDone = it }
    }
    if (onboardingDone == null) {
        Box(Modifier.fillMaxSize()) {}
        return
    }
    val start = when {
        onboardingDone == false -> "splash"
        else -> "main"
    }
    NavHost(navController = nav, startDestination = start) {
        composable("splash") { SplashScreen { nav.navigate("onboarding") { popUpTo("splash") { inclusive = true } } } }
        composable("onboarding") { OnboardingScreen { scope.launch { UserPreferences.setOnboardingDone(ctx, true); nav.navigate("voice_enroll") { popUpTo("onboarding") { inclusive = true } } } } }
        composable("voice_enroll") { VoiceEnrollmentScreen { nav.navigate("permission_setup") { popUpTo("voice_enroll") { inclusive = true } } } }
        composable("permission_setup") { PermissionSetupScreen { nav.navigate("main") { popUpTo("permission_setup") { inclusive = true } } } }
        composable("main") { MainScaffold() }
        // deep links for direct nav
        composable("history") { HistoryScreen() }
        composable("memory") { MemoryScreen() }
        composable("tools") { ToolsScreen(onNavigate = { nav.navigate(it) }) }
        composable("notifications") { NotificationsScreen() }
        composable("automation") { AutomationScreen() }
        composable("activity_logs") { ActivityLogsScreen() }
        composable("privacy") { PrivacyScreen() }
        composable("security") { SecurityScreen() }
        composable("voice_settings") { VoiceSettingsScreen() }
        composable("ai_settings") { AiSettingsScreen() }
        composable("permission_manager") { PermissionManagerScreen() }
        composable("language") { LanguageScreen() }
        composable("appearance") { AppearanceScreen() }
        composable("about") { AboutScreen() }
    }
}

@Composable
private fun MainScaffold() {
    var selected by remember { mutableStateOf(Tab.Home) }
    var subRoute by remember { mutableStateOf<String?>(null) }
    if (subRoute != null) {
        Box(Modifier.fillMaxSize()) {
            when (subRoute) {
                "history" -> HistoryScreen()
                "memory" -> MemoryScreen()
                "tools_extra" -> ToolsScreen(onNavigate = { subRoute = it })
                "notifications" -> NotificationsScreen()
                "automation" -> AutomationScreen()
                "activity_logs" -> ActivityLogsScreen()
                "privacy" -> PrivacyScreen()
                "security" -> SecurityScreen()
                "voice_settings" -> VoiceSettingsScreen()
                "ai_settings" -> AiSettingsScreen()
                "permission_manager" -> PermissionManagerScreen()
                "language" -> LanguageScreen()
                "appearance" -> AppearanceScreen()
                "about" -> AboutScreen()
                else -> {}
            }
            androidx.compose.material3.Button(onClick = { subRoute = null }, modifier = Modifier.padding(12.dp)) { Text("Back") }
        }
        return
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (selected) {
                Tab.Home -> HomeScreen(onNavigate = { subRoute = it })
                Tab.Chat -> ChatScreen()
                Tab.Voice -> VoiceScreen()
                Tab.Settings -> SettingsScreen(onNavigate = { subRoute = it })
            }
        }
    }
}
