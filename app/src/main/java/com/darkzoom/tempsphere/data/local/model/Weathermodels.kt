package com.darkzoom.tempsphere.data.local.model

import androidx.compose.ui.graphics.Color

enum class ForecastTab {
    HOURLY, WEEKLY
}

enum class WeatherType {
    SUNNY, PARTLY_CLOUDY, CLOUDY, RAINY, THUNDER, NIGHT, SNOWY, FOGGY
}

data class HourlyWeather(
    val time: String,
    val tempF: Int,
    val type: WeatherType,
    val precipPct: Int
)

data class DailyWeather(
    val day: String,
    val high: Int,
    val low: Int,
    val type: WeatherType,
    val precipPct: Int
)

data class SavedLocation(
    val id: Int,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val temp: Int,
    val feelsLike: Int,
    val high: Int,
    val low: Int,
    val description: String,
    val time: String,
    val type: WeatherType,
    val isCurrent: Boolean = false,
    val gradientColors: List<Color>
)



