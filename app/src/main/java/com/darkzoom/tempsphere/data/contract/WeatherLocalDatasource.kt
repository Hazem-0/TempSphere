package com.darkzoom.tempsphere.data.contract

import CurrentWeatherEntity
import ForecastItemEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDatasource {
    suspend fun cacheCurrentWeather(entity: CurrentWeatherEntity)
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>
    suspend fun clearCurrentWeather(lat: Double, lon: Double, units: String, lang: String)
    suspend fun clearAllCurrentWeather()
    suspend fun clearAllForecast()
    suspend fun cacheForecast(items: List<ForecastItemEntity>)
    fun getForecast(): Flow<List<ForecastItemEntity>>
    suspend fun clearForecast(lat: Double, lon: Double, units: String, lang: String)
}