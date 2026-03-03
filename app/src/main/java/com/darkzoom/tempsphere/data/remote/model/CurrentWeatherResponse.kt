package com.darkzoom.tempsphere.data.remote.model

data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<WeatherDescription>,
    val base: String,
    val main: MainWeather,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: SysCurrent,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class SysCurrent(
    val type: Int?,
    val id: Int?,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)