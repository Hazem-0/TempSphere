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

    fun getCurrentWeather(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<CurrentWeatherEntity?> =
        currentWeatherDao.getCurrentWeather(lat, lon, units, lang)

    suspend fun clearCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        currentWeatherDao.deleteCurrentWeather(lat, lon, units, lang)
    }


    suspend fun cacheForecast(items: List<ForecastItemEntity>) {
        forecastDao.insertForecastItems(items)
    }

    fun getForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<List<ForecastItemEntity>> =
        forecastDao.getForecast(lat, lon, units, lang)

    suspend fun clearForecast(lat: Double, lon: Double, units: String, lang: String) {
        forecastDao.deleteForecast(lat, lon, units, lang)
    }
}