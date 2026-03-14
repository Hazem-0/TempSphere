package com.darkzoom.tempsphere.data.local.model

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

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    data class Success(
        val city: String,
        val temp: Int,
        val feelsLike: Int,
        val high: Int,
        val low: Int,
        val description: String,
        val weatherType: WeatherType,
        val humidity: Int,
        val windMs: Float,
        val pressureHpa: Int,
        val cloudinessPct: Int,
        val dateLabel: String,
        val hourly: List<HourlyWeather>,
        val daily: List<DailyWeather>,
        val isRefreshing: Boolean = false
    ) : HomeUiState()
}