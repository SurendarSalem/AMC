package com.amc.amcapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill


@Composable
fun CurvedBackground(color: Color, modifier: Modifier) {
    Canvas(
        modifier
    ) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(0f, height * 0.6f)
            quadraticBezierTo(width / 2, height * 1.05f, width, height * 0.7f)
            lineTo(width, 0f)
            lineTo(0f, 0f)
            close()
        }

        drawPath(path = path, color = color, style = Fill)
    }
}