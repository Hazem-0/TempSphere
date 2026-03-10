package com.darkzoom.tempsphere.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.common.components.WeatherIllustration
import com.darkzoom.tempsphere.data.remote.model.HourlyWeather

@Composable
fun HourlyForecastRow(
    items: List<HourlyWeather>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(items) { index, item ->
            HourlyCard(item = item, isNow = index == 0)
        }
    }
}

@Composable
private fun HourlyCard(item: HourlyWeather, isNow: Boolean) {
    val colors = LocalAppTheme.current
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .width(72.dp)
            .clip(shape)
            .then(
                if (isNow) {
                    Modifier.background(
                        Brush.linearGradient(
                            listOf(colors.accentPrimary.copy(alpha = 0.32f), colors.accentSecondary.copy(alpha = 0.14f))
                        )
                    )
                } else {
                    Modifier.background(colors.glassBg)
                }
            )
            .border(
                width = 1.dp,
                color = if (isNow) colors.accentPrimary.copy(alpha = 0.45f) else colors.glassBorder,
                shape = shape
            )
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            text = item.time,
            color = if (isNow) colors.accentPrimary else colors.textSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.3.sp
        )

        WeatherIllustration(type = item.type, size = 34.dp)

        Text(
            text = "${item.tempF}°",
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        if (item.precipPct > 0) {
            Text(
                text = "${item.precipPct}%",
                color = colors.accentSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
        }
    }
}