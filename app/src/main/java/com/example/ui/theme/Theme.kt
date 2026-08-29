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
    onPrimary = MedicalTealDark,
    primaryContainer = MedicalTealDark,
    onPrimaryContainer = MedicalTealLight,
    secondary = SafeBlueLight,
    onSecondary = SafeBlueDark,
    background = Color(0xFF101C1A),
    surface = Color(0xFF162523),
    onBackground = Color(0xFFE3EFEA),
    onSurface = Color(0xFFE3EFEA),
    surfaceVariant = Color(0xFF1F3330),
    onSurfaceVariant = Color(0xFFB5C9C4)
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTealPrimary,
    onPrimary = Color.White,
    primaryContainer = MedicalTealLight,
    onPrimaryContainer = MedicalTealDark,
    secondary = SafeBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = SafeBlueLight,
    onSecondaryContainer = SafeBlueDark,
    tertiary = DutyPharmacyOrange,
    background = MedicalBackgroundLight,
    surface = MedicalSurfaceWhite,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = MedicalSurfaceVariant,
    onSurfaceVariant = TextSecondaryMuted,
    outline = BorderSoft
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
