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
    primary = EmeraldAccent,
    onPrimary = DarkSlate,
    primaryContainer = EmeraldPrimary,
    onPrimaryContainer = Color.White,
    secondary = EmeraldLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A2E),
    onSecondaryContainer = EmeraldMint,
    tertiary = AccentGold,
    background = DarkSlate,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkCardBg,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldMint,
    onPrimaryContainer = EmeraldDark,
    secondary = EmeraldLight,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = EmeraldDark,
    tertiary = AccentGold,
    background = LightBackground,
    onBackground = TextPrimaryDark,
    surface = LightSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep tailored brand emerald palette for fintech clarity
    content: @Composable () -> Unit,
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
