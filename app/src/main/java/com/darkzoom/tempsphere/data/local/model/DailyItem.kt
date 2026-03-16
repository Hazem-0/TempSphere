package com.darkzoom.tempsphere.data.local.model

data class DailyItem(
    val dayLabel: String,
    val high: Int,
    val low: Int,
    val type: WeatherType,
    val description: String
)
