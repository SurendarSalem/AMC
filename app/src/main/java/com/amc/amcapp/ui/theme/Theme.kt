package com.amc.amcapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF410000),

    secondary = Color(0xFFF58F86),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF58F86),
    onSecondaryContainer = Color(0xFF263238),

    tertiary = Color(0xFF8E24AA),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE1BEE7),
    onTertiaryContainer = Color(0xFF38004D),

    error = Color(0xFFB00020),
    onError = Color.White,

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),

    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F)
)

// 🌑 Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEF5350),       // Softer red
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB71C1C),
    onPrimaryContainer = Color(0xFFFFCDD2),

    secondary = Color(0xFFF58F86),     // Blue-Grey
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFF58F86),
    onSecondaryContainer = Color(0xFFCFD8DC),

    tertiary = Color(0xFFCE93D8),      // Purple accent
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF6A1B9A),
    onTertiaryContainer = Color(0xFFE1BEE7),

    error = Color(0xFFCF6679),
    onError = Color.Black,

    background = Color(0xFF121212),
    onBackground = Color(0xFFEDEDED),

    surface = Color(0xFF121212),
    onSurface = Color(0xFFEDEDED)
)

@Composable
fun AMCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,        // you can define your Shapes.kt
        content = content
    )
}
