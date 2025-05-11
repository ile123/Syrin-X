package com.ile.syrin_x.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

enum class AppTheme(val displayName: String) {
    SystemDefault("System Default"),
    Light("Light"),
    Dark("Dark"),
    OceanBreeze("Ocean Breeze"),
    SunsetGlow("Sunset Glow"),
    ForestWhisper("Forest Whisper"),
    LavenderMist("Lavender Mist"),
    SlateSerenity("Slate Serenity")
}

private val LightColorScheme = lightColorScheme(
    primary = SoftTeal,
    onPrimary = OnSoftTeal,
    secondary = LightBeige,
    onSecondary = OnLightBeige,
    tertiary = WarmBrown,
    onTertiary = OnWarmBrown,
    surface = PureWhite,
    onSurface = OnPureWhite,
    background = PureWhite,
    onBackground = OnPureWhite,
    error = CrimsonRed,
    onError = onCrimsonRed,
    outline = DisabledLight
)

private val DarkColorScheme = darkColorScheme(
    primary = DeepBlue,
    onPrimary = PureWhite,
    secondary = MediumBlue,
    onSecondary = PureWhite,
    tertiary = SoftBlue,
    onTertiary = DarkBlack,
    surface = Charcoal,
    onSurface = PureWhite,
    background = DarkBlack,
    onBackground = PureWhite,
    error = CrimsonRed,
    onError = PureWhite,
    outline = DisabledDark
)

private val OceanBreezeLightColorScheme = lightColorScheme(
    primary = SoftTeal,
    onPrimary = OnSoftTeal,
    secondary = LightBeige,
    onSecondary = OnLightBeige,
    tertiary = PaleBlue,
    onTertiary = onPaleBlue,
    background = PureWhite,
    onBackground = OnPureWhite,
    surface = PureWhite,
    onSurface = OnPureWhite,
    error = CrimsonRed,
    onError = onCrimsonRed,
    outline = OceanBreezeDisabledLight
)

private val OceanBreezeDarkColorScheme = darkColorScheme(
    primary = SoftTeal,
    onPrimary = PureWhite,
    secondary = Slate,
    onSecondary = onSlate,
    tertiary = SoftBlue,
    onTertiary = onSoftBlue,
    background = DarkBlack,
    onBackground = PureWhite,
    surface = Charcoal,
    onSurface = PureWhite,
    error = CrimsonRed,
    onError = PureWhite,
    outline = OceanBreezeDisabledDark
)

private val SunsetGlowLightColorScheme = lightColorScheme(
    primary = WarmBrown,
    onPrimary = OnWarmBrown,
    secondary = LightBeige,
    onSecondary = OnLightBeige,
    tertiary = Pink80,
    onTertiary = OnPureWhite,
    background = PureWhite,
    onBackground = OnPureWhite,
    surface = LightBeige,
    onSurface = OnLightBeige,
    error = CrimsonRed,
    onError = onCrimsonRed,
    outline = SunsetGlowDisabledLight
)

private val SunsetGlowDarkColorScheme = darkColorScheme(
    primary = WarmBrown,
    onPrimary = PureWhite,
    secondary = StoneGray,
    onSecondary = onStoneGray,
    tertiary = Pink40,
    onTertiary = OnPureWhite,
    background = DarkBlack,
    onBackground = PureWhite,
    surface = Charcoal,
    onSurface = PureWhite,
    error = CrimsonRed,
    onError = PureWhite,
    outline = SunsetGlowDisabledDark
)

private val ForestWhisperLightColorScheme = lightColorScheme(
    primary = DeepBrown,
    onPrimary = OnDeepBrown,
    secondary = WarmBrown,
    onSecondary = OnWarmBrown,
    tertiary = MutedBlue,
    onTertiary = onMutedBlue,
    background = LightBeige,
    onBackground = OnLightBeige,
    surface = PureWhite,
    onSurface = OnPureWhite,
    error = CrimsonRed,
    onError = onCrimsonRed,
    outline = ForestWhisperDisabledLight
)

private val ForestWhisperDarkColorScheme = darkColorScheme(
    primary = DeepBrown,
    onPrimary = PureWhite,
    secondary = AshGray,
    onSecondary = onAshGray,
    tertiary = MediumBlue,
    onTertiary = onMediumBlue,
    background = Slate,
    onBackground = onSlate,
    surface = StoneGray,
    onSurface = onStoneGray,
    error = CrimsonRed,
    onError = PureWhite,
    outline = ForestWhisperDisabledDark
)

private val LavenderMistLightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = PureWhite,
    secondary = PurpleGrey40,
    onSecondary = PureWhite,
    tertiary = Pink80,
    onTertiary = OnPureWhite,
    background = PureWhite,
    onBackground = OnPureWhite,
    surface = PureWhite,
    onSurface = OnPureWhite,
    error = CrimsonRed,
    onError = onCrimsonRed,
    outline = LavenderMistDisabledLight
)

private val LavenderMistDarkColorScheme = darkColorScheme(
    primary = Purple40,
    onPrimary = PureWhite,
    secondary = PurpleGrey40,
    onSecondary = PureWhite,
    tertiary = Pink40,
    onTertiary = OnPureWhite,
    background = DarkBlack,
    onBackground = PureWhite,
    surface = Charcoal,
    onSurface = PureWhite,
    error = CrimsonRed,
    onError = PureWhite,
    outline = LavenderMistDisabledDark
)

private val SlateSerenityLightColorScheme = lightColorScheme(
    primary = StoneGray,
    onPrimary = onStoneGray,
    secondary = LightGray,
    onSecondary = onLightGray,
    tertiary = PaleCornflowerBlue,
    onTertiary = onPaleCornflowerBlue,
    background = PureWhite,
    onBackground = OnPureWhite,
    surface = PureWhite,
    onSurface = OnPureWhite,
    error = CrimsonRed,
    onError = onCrimsonRed,
    outline = SlateSerenityDisabledLight
)

private val SlateSerenityDarkColorScheme = darkColorScheme(
    primary = Slate,
    onPrimary = onSlate,
    secondary = MutedCornflowerBlue,
    onSecondary = onMutedCornflowerBlue,
    tertiary = PaleCornflowerBlue,
    onTertiary = onPaleCornflowerBlue,
    background = DarkBlack,
    onBackground = PureWhite,
    surface = Charcoal,
    onSurface = PureWhite,
    error = CrimsonRed,
    onError = PureWhite,
    outline = SlateSerenityDisabledDark
)

@Composable
fun SyrinXTheme(
    appTheme: AppTheme,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val isDark = when (appTheme) {
        AppTheme.SystemDefault -> systemDark
        AppTheme.Light -> false
        AppTheme.Dark -> true
        else -> systemDark
    }

    val colorScheme = when (appTheme) {
        AppTheme.SystemDefault -> if (isDark) DarkColorScheme else LightColorScheme

        AppTheme.Light -> LightColorScheme
        AppTheme.Dark -> DarkColorScheme

        AppTheme.OceanBreeze -> if (isDark) OceanBreezeDarkColorScheme else OceanBreezeLightColorScheme
        AppTheme.SunsetGlow -> if (isDark) SunsetGlowDarkColorScheme else SunsetGlowLightColorScheme
        AppTheme.ForestWhisper -> if (isDark) ForestWhisperDarkColorScheme else ForestWhisperLightColorScheme
        AppTheme.LavenderMist -> if (isDark) LavenderMistDarkColorScheme else LavenderMistLightColorScheme
        AppTheme.SlateSerenity -> if (isDark) SlateSerenityDarkColorScheme else SlateSerenityLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}
