package com.darkzoom.tempsphere.data.local.datasource


import com.darkzoom.tempsphere.data.contract.WeatherLocalDatasource
import com.darkzoom.tempsphere.data.local.dao.CurrentWeatherDao
import com.darkzoom.tempsphere.data.local.dao.ForecastDao
import com.darkzoom.tempsphere.data.local.model.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.data.local.model.entity.ForecastItemEntity

import kotlinx.coroutines.flow.Flow

class WeatherLocalDatasourceImp(
    private val currentWeatherDao: CurrentWeatherDao,
    private val forecastDao: ForecastDao
) : WeatherLocalDatasource {

    override suspend fun cacheCurrentWeather(entity: CurrentWeatherEntity) {
        currentWeatherDao.insertCurrentWeather(entity)
    }

    override fun getCurrentWeather(): Flow<CurrentWeatherEntity?> =
        currentWeatherDao.getCurrentWeather()

    override suspend fun clearCurrentWeather(lat: Double, lon: Double, units: String, lang: String) {
        currentWeatherDao.deleteCurrentWeather(lat, lon, units, lang)
    }

    override suspend fun clearAllCurrentWeather() {
        currentWeatherDao.deleteAllCurrentWeather()
    }

    override suspend fun clearAllForecast() {
        forecastDao.deleteAllForecast()
    }

    override suspend fun cacheForecast(items: List<ForecastItemEntity>) {
        forecastDao.insertForecastItems(items)
    }

    override fun getForecast(): Flow<List<ForecastItemEntity>> =
        forecastDao.getForecast()

    override suspend fun clearForecast(lat: Double, lon: Double, units: String, lang: String) {
        forecastDao.deleteForecast(lat, lon, units, lang)
    }
}