package com.lihan.vibeplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lihan.vibeplayer.R


val HostGrotesk = FontFamily(
    fonts = listOf(
        Font(R.font.hostgrotesk_extrabold , FontWeight.ExtraBold),
        Font(R.font.hostgrotesk_bold , FontWeight.Bold),
        Font(R.font.hostgrotesk_semibold , FontWeight.SemiBold),
        Font(R.font.hostgrotesk_medium , FontWeight.Medium),
        Font(R.font.hostgrotesk_regular , FontWeight.Normal),
        Font(R.font.hostgrotesk_light , FontWeight.Light),
    )
)


// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        color = TextPrimary
    ),

    bodyLarge = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = HostGrotesk,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = TextPrimary
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)