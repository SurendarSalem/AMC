package com.amc.amcapp.ui.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.amc.amcapp.R
import com.amc.amcapp.ui.theme.Red

@Composable
fun CurvedBanner() {
    Box {
        CurvedBackground(
            color = Red, modifier = Modifier
                .fillMaxWidth()
                .align(
                    Alignment.TopCenter
                )
                .height(120.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.gym),
            contentDescription = "A description of my image",
            modifier = Modifier
                .size(90.dp)
                .offset(y = 65.dp)
                .align(Alignment.TopCenter)
                .clip(CircleShape)
                .border(
                    width = 1.dp, color = Color(0xFF6650a4), // Purple border
                    shape = CircleShape
                )
        )
    }
}