package com.darkzoom.tempsphere.data.local.model

data class PlaceDetailData(
    val city: String,
    val country: String,
    val temp: Int,
    val feelsLike: Int,
    val high: Int,
    val low: Int,
    val description: String,
    val humidity: Int,
    val windMs: Double,
    val pressureHpa: Int,
    val cloudinessPct: Int,
    val weatherType: WeatherType,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val dateLabel: String,
    val isRefreshing: Boolean = false
)