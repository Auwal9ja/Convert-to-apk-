package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = ElegantPrimaryDark,
    secondary = ElegantSecondaryDark,
    tertiary = ElegantTertiaryDark,
    background = ElegantBackgroundDark,
    surface = ElegantSurfaceDark,
    outline = ElegantOutlineDark,
    onPrimary = Color(0xFF071411),       // Dark forest-green for text on gold
    onSecondary = Color(0xFFFFFFFF),     // White text on teal/mint
    onBackground = Color(0xFFE2EAE7),    // Light sage-charcoal text
    onSurface = Color(0xFFE2EAE7),       // Light sage-charcoal text
    onSurfaceVariant = Color(0xFFAEC4BE) // Muted sage-teal
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimary,
    secondary = GoldSecondary,
    tertiary = MintTertiary,
    background = LightBackground,
    surface = LightSurface,
    outline = Color(0xFFD2E3DE),
    onPrimary = Color.White,
    onSecondary = Color(0xFF132D27),
    onBackground = Color(0xFF132D27),
    onSurface = Color(0xFF132D27),
    onSurfaceVariant = Color(0xFF4A6B63)
  )


@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is disabled by default to show our custom Emerald/Gold branding
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
