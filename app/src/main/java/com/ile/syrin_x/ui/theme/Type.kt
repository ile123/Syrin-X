package com.ile.syrin_x.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ile.syrin_x.R

val PacificoFamily = FontFamily(
    Font(R.font.pacifico_regular, weight = FontWeight.Normal)
)

val JosefSlabFamily = FontFamily(
    Font(R.font.josefinslab_bold_italic, weight = FontWeight.Bold),
    Font(R.font.josefinslab_semibold_italic, weight = FontWeight.SemiBold),
    Font(R.font.josefinslab_italic, weight = FontWeight.Normal),
    Font(R.font.josefinslab_italic_variablefont_wght, weight = FontWeight.Normal),
    Font(R.font.josefinslab_medium_italic, weight = FontWeight.Medium),
    Font(R.font.josefinslab_light_italic, weight = FontWeight.Light),
    Font(R.font.josefinslab_thin_italic, weight = FontWeight.Thin),
)

val QuickSandFamily = FontFamily(
    Font(R.font.quicksand_bold, weight = FontWeight.Bold),
    Font(R.font.quicksand_semibold, weight = FontWeight.SemiBold),
    Font(R.font.quicksand_regular, weight = FontWeight.Normal),
    Font(R.font.quicksand_variablefont_wght, weight = FontWeight.Normal),
    Font(R.font.quicksand_medium, weight = FontWeight.Medium),
    Font(R.font.quicksand_light, weight = FontWeight.Light)
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PacificoFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 57.sp,
        lineHeight  = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PacificoFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 45.sp,
        lineHeight  = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = PacificoFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 36.sp,
        lineHeight  = 44.sp,
        letterSpacing = 0.sp
    ),

    headlineLarge = TextStyle(
        fontFamily = JosefSlabFamily,
        fontWeight  = FontWeight.Bold,
        fontSize    = 32.sp,
        lineHeight  = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = JosefSlabFamily,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 28.sp,
        lineHeight  = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = JosefSlabFamily,
        fontWeight  = FontWeight.Medium,
        fontSize    = 24.sp,
        lineHeight  = 32.sp,
        letterSpacing = 0.sp
    ),

    titleLarge = TextStyle(
        fontFamily = JosefSlabFamily,
        fontWeight  = FontWeight.Medium,
        fontSize    = 22.sp,
        lineHeight  = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = JosefSlabFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = JosefSlabFamily,
        fontWeight  = FontWeight.Light,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = QuickSandFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = QuickSandFamily,
        fontWeight  = FontWeight.Medium,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = QuickSandFamily,
        fontWeight  = FontWeight.Light,
        fontSize    = 12.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.4.sp
    ),

    labelLarge = TextStyle(
        fontFamily = QuickSandFamily,
        fontWeight  = FontWeight.Medium,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = QuickSandFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 12.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = QuickSandFamily,
        fontWeight  = FontWeight.Bold,
        fontSize    = 11.sp,
        lineHeight  = 16.sp,
        letterSpacing = 0.5.sp
    )
)