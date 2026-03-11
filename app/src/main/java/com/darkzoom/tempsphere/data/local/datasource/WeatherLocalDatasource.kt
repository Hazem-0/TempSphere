package com.darkzoom.tempsphere.data.local.datasource

import com.darkzoom.tempsphere.data.local.dao.CurrentWeatherDao
import com.darkzoom.tempsphere.data.local.dao.ForecastDao
import com.darkzoom.tempsphere.data.local.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.data.local.entity.ForecastItemEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDatasource(
    private val currentWeatherDao: CurrentWeatherDao,
    private val forecastDao: ForecastDao
) {



    suspend fun cacheCurrentWeather(entity: CurrentWeatherEntity) {
        currentWeatherDao.insertCurrentWeather(entity)
    }

    fun getCurrentWeather(): Flow<CurrentWeatherEntity?> =
        currentWeatherDao.getCurrentWeather()

    suspend fun clearCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        currentWeatherDao.deleteCurrentWeather(lat, lon, units, lang)
    }

    suspend fun clearAllCurrentWeather() {
        currentWeatherDao.deleteAllCurrentWeather()
    }

    suspend fun clearAllForecast() {
        forecastDao.deleteAllForecast()
    }


    suspend fun cacheForecast(items: List<ForecastItemEntity>) {
        forecastDao.insertForecastItems(items)
    }

    fun getForecast(): Flow<List<ForecastItemEntity>> =
        forecastDao.getForecast()

    suspend fun clearForecast(lat: Double, lon: Double, units: String, lang: String) {
        forecastDao.deleteForecast(lat, lon, units, lang)
    }

}