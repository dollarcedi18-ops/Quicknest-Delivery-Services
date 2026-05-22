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

private val DarkColorScheme = darkColorScheme(
    primary = EarthBrownDark,
    onPrimary = DarkClay,
    primaryContainer = DarkCardBg,
    onPrimaryContainer = WarmSandDark,
    secondary = SoftTaupeDark,
    onSecondary = DarkClay,
    secondaryContainer = DarkClay,
    onSecondaryContainer = CreamyText,
    tertiary = BrickRed,
    onTertiary = DarkClay,
    background = DarkClay,
    onBackground = CreamyText,
    surface = DarkCardBg,
    onSurface = CreamyText,
    surfaceVariant = DarkPeachBorder,
    onSurfaceVariant = SoftTaupeDark,
    outline = DarkPeachBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EarthBrown,
    onPrimary = Color.White,
    primaryContainer = CardBgWarm,
    onPrimaryContainer = EarthBrown,
    secondary = MutedTaupe,
    onSecondary = Color.White,
    secondaryContainer = SoftSand,
    onSecondaryContainer = DarkCharcoal,
    tertiary = TerraCotta,
    onTertiary = Color.White,
    background = WarmCream,
    onBackground = DarkCharcoal,
    surface = Color.White,
    onSurface = DarkCharcoal,
    surfaceVariant = Color(0xFFF4EFEA), // Very light warm gray
    onSurfaceVariant = MutedTaupe,
    outline = LightPeach
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamicColor by default to guarantee the elegant Natural Tones theme
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

