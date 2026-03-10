package com.darkzoom.tempsphere.ui.core.Theme


import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.darkzoom.tempsphere.ui.theme.AccentBlue
import com.darkzoom.tempsphere.ui.theme.AccentEmerald
import com.darkzoom.tempsphere.ui.theme.AccentPink
import com.darkzoom.tempsphere.ui.theme.AccentPurple

data class AppThemeColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val glassBg: Color,
    val glassBorder: Color,
    val accentPrimary: Color,
    val accentSecondary: Color,
    val tempGradient: List<Color>,
    val humidityIcon: Color,
    val windIcon: Color,
    val pressureIcon: Color,
    val cloudIcon: Color
)

val NightColors = AppThemeColors(
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.65f),
    glassBg = Color.White.copy(alpha = 0.08f),
    glassBorder = Color.White.copy(alpha = 0.15f),
    accentPrimary = AccentPurple,
    accentSecondary = AccentBlue,
    tempGradient = listOf(AccentBlue, AccentPurple, AccentPink),
    humidityIcon = AccentBlue,
    windIcon = AccentEmerald,
    pressureIcon = AccentPink,
    cloudIcon = AccentPurple
)

val MorningColors = AppThemeColors(
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.85f),
    glassBg = Color.Black.copy(alpha = 0.12f),
    glassBorder = Color.White.copy(alpha = 0.25f),
    accentPrimary = Color(0xFFFFC107),
    accentSecondary = Color(0xFF81C784),
    tempGradient = listOf(Color.White, Color(0xFFAED581), Color(0xFF81C784)),
    humidityIcon = Color(0xFF4FC3F7),
    windIcon = Color(0xFF81C784),
    pressureIcon = Color(0xFFBA68C8),
    cloudIcon = Color(0xFFFFD54F)
)

val AfternoonColors = AppThemeColors(
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.8f),
    glassBg = Color.Black.copy(alpha = 0.20f),
    glassBorder = Color.White.copy(alpha = 0.20f),
    accentPrimary = Color(0xFFFDE047),
    accentSecondary = Color(0xFFFDBA74),
    tempGradient = listOf(Color(0xFFFDE047), Color(0xFFFB923C), Color(0xFFE11D48)),
    humidityIcon = Color(0xFF93C5FD),
    windIcon = Color(0xFF6EE7B7),
    pressureIcon = Color(0xFFF9A8D4),
    cloudIcon = Color(0xFFFDE047)
)

val LocalAppTheme = compositionLocalOf { NightColors }