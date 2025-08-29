package com.amc.amcapp.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class Dimens(
    // Spacing
    val spacingExtraSmall: Float,
    val spacingSmall: Float,
    val spacingMedium: Float,
    val spacingLarge: Float,
    val spacingExtraLarge: Float,

    // Corner radius
    val cornerSmall: Float,
    val cornerMedium: Float,
    val cornerLarge: Float,

    // Text sizes
    val textSmall: Float,
    val textMedium: Float,
    val textLarge: Float,
    val textExtraLarge: Float,

    val tileSize: Float,
    val tagTextSize: Float,
    val tagPadding: Float
)


val VerySmallDimens = Dimens(
    spacingExtraSmall = 2f,
    spacingSmall = 4f,
    spacingMedium = 8f,
    spacingLarge = 12f,
    spacingExtraLarge = 20f,

    cornerSmall = 4f,
    cornerMedium = 8f,
    cornerLarge = 10f,

    textSmall = 12f,
    textMedium = 14f,
    textLarge = 16f,
    textExtraLarge = 18f,
    tileSize = 120f,
    tagTextSize = 10f,
    tagPadding = 0f
)

// Phone
val SmallDimens = Dimens(
    spacingExtraSmall = 2f,
    spacingSmall = 4f,
    spacingMedium = 8f,
    spacingLarge = 16f,
    spacingExtraLarge = 24f,

    cornerSmall = 4f,
    cornerMedium = 8f,
    cornerLarge = 12f,

    textSmall = 12f,
    textMedium = 14f,
    textLarge = 18f,
    textExtraLarge = 22f,
    tileSize = 160f,
    tagTextSize = 12f,
    tagPadding = 0f
)

// Tablet
val MediumDimens = Dimens(
    spacingExtraSmall = 4f,
    spacingSmall = 8f,
    spacingMedium = 12f,
    spacingLarge = 20f,
    spacingExtraLarge = 32f,

    cornerSmall = 6f,
    cornerMedium = 10f,
    cornerLarge = 14f,

    textSmall = 14f,
    textMedium = 16f,
    textLarge = 20f,
    textExtraLarge = 24f,
    tileSize = 160f,
    tagTextSize = 12f,
    tagPadding = 0f
)

// Large Screen / Desktop
val LargeDimens = Dimens(
    spacingExtraSmall = 6f,
    spacingSmall = 10f,
    spacingMedium = 16f,
    spacingLarge = 24f,
    spacingExtraLarge = 36f,

    cornerSmall = 8f,
    cornerMedium = 12f,
    cornerLarge = 16f,

    textSmall = 16f,
    textMedium = 18f,
    textLarge = 22f,
    textExtraLarge = 28f,
    tileSize = 160f,
    tagTextSize = 12f,
    tagPadding = 0f
)

val LocalDimens = staticCompositionLocalOf { SmallDimens }