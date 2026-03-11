package com.darkzoom.tempsphere.ui.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.tooling.preview.Preview
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme


@Composable
fun MorningBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFF1A6FA8),
                    0.25f to Color(0xFF4D9FD6),
                    0.55f to Color(0xFF9ECFEF),
                    0.78f to Color(0xFFFFD98E),
                    1.00f to Color(0xFFFFBC6B)
                )
            )
        )




        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFEDD5).copy(alpha = 0.40f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.92f), radius = w * 0.7f
            ),
            topLeft = Offset(-w * 0.2f, h * 0.82f),
            size    = Size(w * 1.4f, h * 0.20f)
        )


    }
}



@Composable
fun NightBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFF020408),
                    0.20f to Color(0xFF060C1A),
                    0.50f to Color(0xFF0C1535),
                    0.80f to Color(0xFF111528),
                    1.00f to Color(0xFF0A0C18)
                )
            )
        )


    }
}

@Composable
fun AfterNoonBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to Color(0xFF0D3B6E),
                    0.20f to Color(0xFF1A5FA8),
                    0.42f to Color(0xFF4A8FD4),
                    0.62f to Color(0xFFDE6B35),
                    0.82f to Color(0xFFFF9A28),
                    1.00f to Color(0xFFFFCC44)
                )
            )
        )


    }

}
@Preview(name = "Morning Background", showSystemUi = true)
@Composable
private fun MorningBackgroundPreview() {
    TempSphereTheme { MorningBackground() }
}

@Preview(name = "Night Background", showSystemUi = true)
@Composable
private fun NightBackgroundPreview() {
    TempSphereTheme { NightBackground() }
}

@Preview(name = "Afternoon Background", showSystemUi = true)
@Composable
private fun AfterNoonBackgroundPreview() {
    TempSphereTheme { AfterNoonBackground() }
}