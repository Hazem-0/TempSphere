package com.darkzoom.tempsphere.ui.settings.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassToggle(
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (enabled) 23.dp else 2.dp,
        animationSpec = tween(300),
        label = "toggle_offset"
    )

    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .width(46.dp)
            .height(25.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .background(
                if (enabled) Brush.linearGradient(listOf(color, color.copy(alpha = 0.8f)))
                else Brush.linearGradient(listOf(onSurface.copy(alpha = 0.12f), onSurface.copy(alpha = 0.12f)))
            )
            .border(
                1.dp,
                if (enabled) color.copy(alpha = 0.5f) else onSurface.copy(alpha = 0.15f),
                RoundedCornerShape(50)
            )
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .align(Alignment.CenterStart)
                .size(19.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}