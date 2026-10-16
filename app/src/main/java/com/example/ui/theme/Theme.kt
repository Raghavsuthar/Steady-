package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SteadyLightColorScheme = lightColorScheme(
    primary = SagePrimaryDeep,
    onPrimary = PureWhite,
    primaryContainer = SageLight,
    onPrimaryContainer = SagePrimaryDeep,
    secondary = DustyBlueDark,
    onSecondary = PureWhite,
    secondaryContainer = BlueSurface,
    onSecondaryContainer = CharcoalText,
    tertiary = WarmAmber,
    onTertiary = CharcoalText,
    background = CreamBackground,
    onBackground = CharcoalText,
    surface = PureWhite,
    onSurface = CharcoalText,
    surfaceVariant = SandLight,
    onSurfaceVariant = MutedText,
    outline = Color(0xFFD8DCE6)
)

private val SteadyDarkColorScheme = darkColorScheme(
    primary = SageLight,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = SageLight,
    secondary = DustyBlue,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = LightText,
    tertiary = WarmAmber,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = LightText,
    surface = DarkSurface,
    onSurface = LightText,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = MutedLightText,
    outline = Color(0xFF425252)
)

@Composable
fun SteadyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SteadyDarkColorScheme else SteadyLightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = SteadyTypography, content = content)
}

// Retain alias for any template references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    SteadyTheme(darkTheme = darkTheme, content = content)
}
