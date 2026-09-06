package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MedicalEmeraldAccent,
    onPrimary = Color.White,
    primaryContainer = MedicalTealPrimary,
    onPrimaryContainer = Color.White,
    secondary = SafeBlueSecondary,
    onSecondary = Color.White,
    background = Color(0xFF0C1715),
    surface = Color(0xFF132420),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B302B),
    onSurfaceVariant = Color.White,
    outline = Color(0xFF26453D)
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTealPrimary,
    onPrimary = Color.White,
    primaryContainer = MedicalTealDark,
    onPrimaryContainer = Color.White,
    secondary = SafeBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1B302B),
    onSecondaryContainer = Color.White,
    tertiary = DutyPharmacyOrange,
    onTertiary = Color.White,
    background = Color(0xFF0C1715),
    surface = Color(0xFF132420),
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B302B),
    onSurfaceVariant = Color.White,
    outline = Color(0xFF26453D)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature medical branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
