package com.darkzoom.tempsphere.ui.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.remote.model.WeatherType


@Composable
fun WeatherIllustration(
    type: WeatherType,
    size: Dp = 40.dp,
    tint: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val resId = when (type) {
        WeatherType.SUNNY         -> R.drawable.ic_weather_sunny
        WeatherType.PARTLY_CLOUDY -> R.drawable.ic_weather_partly_cloudy
        WeatherType.CLOUDY        -> R.drawable.ic_weather_cloudy
        WeatherType.RAINY         -> R.drawable.ic_weather_rainy
        WeatherType.THUNDER       -> R.drawable.ic_weather_thunder
        WeatherType.NIGHT         -> R.drawable.ic_weather_night
        WeatherType.SNOWY         -> R.drawable.ic_weather_snowy
        WeatherType.FOGGY         -> R.drawable.ic_weather_foggy
    }

    Icon(
        painter           = painterResource(id = resId),
        contentDescription = type.name,
        tint               = tint,
        modifier           = modifier.size(size)
    )
}