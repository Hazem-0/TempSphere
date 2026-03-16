package com.darkzoom.tempsphere.data.repository

import CurrentWeatherEntity
import ForecastItemEntity
import com.darkzoom.tempsphere.data.contract.WeatherLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherLocalDatasource : WeatherLocalDatasource {

    private val _currentWeather = MutableStateFlow<CurrentWeatherEntity?>(null)
    private val _forecast = MutableStateFlow<List<ForecastItemEntity>>(emptyList())

    override fun getCurrentWeather(): Flow<CurrentWeatherEntity?> = _currentWeather
    override fun getForecast(): Flow<List<ForecastItemEntity>> = _forecast

    override suspend fun cacheCurrentWeather(entity: CurrentWeatherEntity) {
        _currentWeather.value = entity
    }

    override suspend fun clearCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        _currentWeather.value = null
    }

    override suspend fun clearAllCurrentWeather() {
        _currentWeather.value = null
    }

    override suspend fun cacheForecast(items: List<ForecastItemEntity>) {
        _forecast.value = items
    }

    override suspend fun clearForecast(lat: Double, lon: Double, units: String, lang: String) {
        _forecast.value = emptyList()
    }

    override suspend fun clearAllForecast() {
        _forecast.value = emptyList()
    }
}