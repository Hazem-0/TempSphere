package com.darkzoom.tempsphere.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.ui.common.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.common.components.WeatherIllustration
import com.darkzoom.tempsphere.data.remote.model.WeatherType

@Composable
fun MainSection(
    tempF: Int,
    feelsLikeF: Int,
    highF: Int,
    lowF: Int,
    description: String,
    weatherType: WeatherType,
    dateLabel: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WeatherIllustration(
            type = weatherType,
            size = 148.dp,
            modifier = Modifier.padding(top = 8.dp)
        )

        val tempGradient = Brush.verticalGradient(
            listOf(colors.textPrimary, colors.accentSecondary, colors.accentPrimary)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(colors.accentPrimary.copy(alpha = 0.20f), Color.Transparent)
                    ),
                    radius = size.minDimension * 0.9f,
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            }
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "$tempF",
                    style = TextStyle(
                        fontSize = 96.sp,
                        fontWeight = FontWeight.ExtraBold,
                        brush = tempGradient,
                        letterSpacing = (-3).sp
                    )
                )
                Text(
                    text = "°F",
                    style = TextStyle(
                        fontSize = 34.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.accentSecondary
                    ),
                    modifier = Modifier.padding(top = 16.dp, start = 2.dp)
                )
            }
        }

        Text(
            text = description,
            color = colors.textPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.8.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = dateLabel,
                color = colors.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp
            )
            Text("  ·  ", color = colors.textSecondary.copy(alpha = 0.5f), fontSize = 12.sp)
            Text(
                text = "Feels ${feelsLikeF}°",
                color = colors.textSecondary,
                fontSize = 12.sp,
                letterSpacing = 0.2.sp
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp, bottom = 8.dp)
        ) {
            Text(
                text = "H: ${highF}°",
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "L: ${lowF}°",
                color = colors.accentSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
            )
        }
    }
}