package com.darkzoom.tempsphere.data.remote.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    val code: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class ForecastItem(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherDescription>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    @SerializedName("dt_txt")
    val dtTxt: String
)

