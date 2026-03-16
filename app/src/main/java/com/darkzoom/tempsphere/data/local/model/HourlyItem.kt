package com.darkzoom.tempsphere.data.local.model

data class HourlyItem(
    val hour: String,
    val temp: Int,
    val type: WeatherType
)