package com.ile.syrin_x.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = SoftTeal,
    onPrimary = OnSoftTeal,
    secondary = LightBeige,
    onSecondary = OnLightBeige,
    surface = PureWhite,
    onSurface = OnPureWhite,
    background = PureWhite,
    onBackground = OnPureWhite,
    tertiary = WarmBrown,
    onTertiary = OnWarmBrown,
    error = CrimsonRed,
    onError = onCrimsonRed
)

// Define the dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = DeepBlue,
    secondary = MediumBlue,
    surface = Charcoal,
    onSurface = PureWhite,
    background = DarkBlack,
    onBackground = PureWhite,
    tertiary = SoftBlue,
    onTertiary = DarkBlack,
    error = CrimsonRed,
    onError = PureWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite
)

@Composable
fun SyrinXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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