package com.softstudio.chat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.io.File

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_bold.tff"))),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_bold.tff"))),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_bold.tff"))),
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    /* Other default text styles to override */
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_bold.tff"))),
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 28.sp,
        letterSpacing = 14.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_bold.tff"))),
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,
        lineHeight = 54.sp,
        letterSpacing = 6.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_bold.tff"))),
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_regular.tff"))),
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_regular.tff"))),
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily(Font(file = File("fonts/josefin_sans_regular.tff"))),
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.5.sp
    )
)