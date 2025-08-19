package com.amc.amcapp.util

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun BubbleProgressBar(
    count: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp,
    spacing: Dp = 8.dp,
    activeIndex: Int? = null,       // if provided, shows that index active
    progress: Float? = null,        // if provided (0f..1f) shows determinate progress
    activeScale: Float = 1.6f,
    inactiveAlpha: Float = 0.35f,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = activeColor.copy(alpha = 0.25f),
    animationDurationMs: Int = 800
) {
    require(count > 0) { "count must be > 0" }

    // Decide mode: determinate (progress != null) > controlled (activeIndex != null) > indeterminate
    val mode = when {
        progress != null -> "determinate"
        activeIndex != null -> "controlled"
        else -> "indeterminate"
    }

    // Indeterminate animation: a floating index from 0..count-1 repeating
    val indeterminateFloat by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = count.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMs * count, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // For determinate map progress -> highest filled index (0-based)
    val filledCount = if (progress != null) {
        // clamp progress 0..1 and convert to number of filled dots
        val p = progress.coerceIn(0f, 1f)
        // e.g. p=0.0 -> 0 filled; p=1.0 -> count filled
        (floor(p * count)).toInt()
    } else 0

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until count) {
            // Decide whether this dot is "active" or "filled"
            val isActive = when (mode) {
                "determinate" -> i < filledCount
                "controlled" -> (activeIndex ?: 0) == i
                else -> {
                    // indeterminate: convert animated float to index
                    val idx = indeterminateFloat.toInt().coerceIn(0, count - 1)
                    idx == i
                }
            }

            // animate scale and alpha smoothly when active changes
            val targetScale = if (isActive) activeScale else 1f
            val scale by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = tween(
                    durationMillis = animationDurationMs,
                    easing = FastOutSlowInEasing
                )
            )
            val targetAlpha = if (isActive) 1f else inactiveAlpha
            val alpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = tween(
                    durationMillis = animationDurationMs,
                    easing = FastOutSlowInEasing
                )
            )

            val color by animateColorAsState(
                targetValue = if (isActive) activeColor else inactiveColor,
                animationSpec = tween(durationMillis = animationDurationMs)
            )

            // convert scaled size to Dp
            val scaledSize = with(LocalDensity.current) { (dotSize * scale).coerceAtLeast(2.dp) }

            Box(
                modifier = Modifier
                    .size(scaledSize)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}
