package com.darkzoom.tempsphere.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkForeground,
    surface = DarkBackground,
    onSurface = DarkForeground,
    primary = DarkPrimary,
    onPrimary = DarkPrimaryForeground,
    secondary = DarkSecondary,
    onSecondary = DarkSecondaryForeground,
    error = DarkDestructive,
    onError = DarkDestructiveForeground,
    surfaceVariant = DarkMuted,
    onSurfaceVariant = DarkMutedForeground,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    background = LightBackground,
    onBackground = LightForeground,
    surface = LightBackground,
    onSurface = LightForeground,
    primary = LightPrimary,
    onPrimary = LightPrimaryForeground,
    secondary = LightSecondary,
    onSecondary = LightSecondaryForeground,
    error = LightDestructive,
    onError = LightDestructiveForeground,
    surfaceVariant = LightMuted,
    onSurfaceVariant = LightMutedForeground,
    outline = LightBorder
)

data class ExtendedColors(
    val chart1: Color, val chart2: Color, val chart3: Color, val chart4: Color, val chart5: Color,
    val sidebar: Color, val sidebarForeground: Color, val sidebarPrimary: Color,
    val sidebarPrimaryForeground: Color, val sidebarAccent: Color, val sidebarAccentForeground: Color
)

val LocalExtendedColors = staticCompositionLocalOf<ExtendedColors> {
    error("No ExtendedColors provided")
}

@Composable
fun TempSphereTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to force your custom CSS colors over Android's wallpaper colors
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

    val extendedColors = if (darkTheme) {
        ExtendedColors(
            chart1 = DarkChart1, chart2 = DarkChart2, chart3 = DarkChart3, chart4 = DarkChart4, chart5 = DarkChart5,
            sidebar = DarkSidebar, sidebarForeground = DarkSidebarForeground, sidebarPrimary = DarkSidebarPrimary,
            sidebarPrimaryForeground = DarkSidebarPrimaryForeground, sidebarAccent = DarkSidebarAccent, sidebarAccentForeground = DarkSidebarAccentForeground
        )
    } else {
        ExtendedColors(
            chart1 = LightChart1, chart2 = LightChart2, chart3 = LightChart3, chart4 = LightChart4, chart5 = LightChart5,
            sidebar = LightSidebar, sidebarForeground = LightSidebarForeground, sidebarPrimary = LightSidebarPrimary,
            sidebarPrimaryForeground = LightSidebarPrimaryForeground, sidebarAccent = LightSidebarAccent, sidebarAccentForeground = LightSidebarAccentForeground
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object TempSphereExtendedTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}