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
    primary = GoldAccent,
    onPrimary = BrandNavyDark,
    primaryContainer = BrandNavyLight,
    onPrimaryContainer = GoldAccentLight,
    secondary = GoldAccentLight,
    onSecondary = BrandNavyDark,
    tertiary = EmeraldProfit,
    background = SlateBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SlateSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF243354),
    onSurfaceVariant = TextSecondaryDark,
    outline = SlateBorderDark,
    error = RubyExpense,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = BrandNavy,
    secondary = GoldAccentDark,
    onSecondary = Color.White,
    tertiary = EmeraldProfit,
    background = SlateBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SlateSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = SlateBorderLight,
    error = RubyExpense,
    onError = Color.White
)

@Composable
fun EasternFlavoursTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep tailored brand restaurant styling
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
