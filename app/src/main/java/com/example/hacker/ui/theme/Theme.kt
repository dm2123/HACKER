package com.example.hacker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HackerDark = darkColorScheme(
    primary = Color(0xFF00FF88),
    secondary = Color(0xFF00CC66),
    tertiary = Color(0xFF33FFCC),
    background = Color(0xFF050805),
    surface = Color(0xFF0A140E),
    surfaceVariant = Color(0xFF102018),
    onPrimary = Color.Black,
    onBackground = Color(0xFFB8FFD9),
    onSurface = Color(0xFFB8FFD9),
    onSurfaceVariant = Color(0xFF7FE3B0)
)

@Composable
fun HackerTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HackerDark,
        content = content
    )
}
