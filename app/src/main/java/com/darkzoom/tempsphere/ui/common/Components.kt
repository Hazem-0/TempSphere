package com.darkzoom.tempsphere.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = 16.dp,
    contentPadding: Dp = 18.dp,
    gradientStartColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
    gradientEndColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
    borderStartColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
    borderMidColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f),
    borderEndColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    shadowColor: Color = Color.Black.copy(alpha = 0.5f),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = elevation, shape = shape, spotColor = shadowColor)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        gradientStartColor,
                        gradientEndColor
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderStartColor,
                        borderMidColor,
                        borderEndColor
                    ),
                    start = Offset.Zero,
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            )
            .padding(contentPadding)
    ) {
        Column(content = content)
    }
}

@Preview(showBackground = true)
@Composable
fun GlassCardPreviewLight() {
    TempSphereTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(32.dp)) {
            GlassCard {
                Text(
                    text = "Light Glass Card",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF252525) // Simulating DarkBackground
@Composable
fun GlassCardPreviewDark() {
    TempSphereTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(32.dp)) {
            GlassCard {
                Text(
                    text = "Dark Glass Card",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}