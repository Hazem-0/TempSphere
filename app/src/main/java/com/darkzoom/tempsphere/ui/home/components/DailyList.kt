package com.darkzoom.tempsphere.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.data.remote.model.DailyWeather
import com.darkzoom.tempsphere.ui.common.components.WeatherIllustration

@Composable
fun DailyForecastList(
    items: List<DailyWeather>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val globalMin = items.minOf { it.low }
    val globalMax = items.maxOf { it.high }
    val range = (globalMax - globalMin).coerceAtLeast(1)

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            DailyRow(
                item = item,
                isToday = index == 0,
                barFraction = (item.high - globalMin).toFloat() / range
            )
        }
    }
}

@Composable
private fun DailyRow(item: DailyWeather, isToday: Boolean, barFraction: Float) {
    val colors = LocalAppTheme.current
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (isToday) {
                    Modifier
                        .background(colors.accentPrimary.copy(alpha = 0.09f))
                        .border(1.dp, colors.accentPrimary.copy(alpha = 0.18f), shape)
                } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.day,
            color = if (isToday) colors.textPrimary else colors.textSecondary,
            fontSize = 14.sp,
            fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal,
            letterSpacing = 0.2.sp,
            modifier = Modifier.width(40.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.width(62.dp)
        ) {
            WeatherIllustration(type = item.type, size = 28.dp)
            if (item.precipPct > 0) {
                Text(
                    text = "${item.precipPct}%",
                    color = colors.accentSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "${item.low}°",
            color = colors.textSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(30.dp)
        )

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .width(68.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.glassBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barFraction.coerceIn(0.08f, 1f))
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.horizontalGradient(colors.tempGradient)
                    )
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = "${item.high}°",
            color = colors.textPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(30.dp)
        )
    }
}