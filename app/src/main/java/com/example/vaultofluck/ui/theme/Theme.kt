package com.example.vaultofluck.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    background = Color(0xFF101018),
    surface = Color(0xFF181820),
    onSurface = Color(0xFFE4E1E6)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    background = Color(0xFFFAF7FF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1625)
)

val LocalLargeText = staticCompositionLocalOf { false }

@Composable
fun VaultOfLuckTheme(
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    CompositionLocalProvider(LocalLargeText provides largeText) {
        MaterialTheme(
            colorScheme = colors,
            typography = if (largeText) MaterialTheme.typography.copy(displayMedium = MaterialTheme.typography.displayMedium.copy(fontSize = MaterialTheme.typography.displayMedium.fontSize * 1.2f)) else MaterialTheme.typography,
            content = content
        )
    }
}
