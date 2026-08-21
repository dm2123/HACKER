package com.example.hacker.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

fun MaterialTheme.hackerLightTheme() = this@hackerLightTheme(
    colorScheme = lightColorScheme(),
    typography = androidx.compose.material3.typography.h5
)

fun MaterialTheme.hackerDarkTheme() = this@hackerDarkTheme(
    colorScheme = darkColorScheme(),
    typography = androidx.compose.material3.typography.h5
)