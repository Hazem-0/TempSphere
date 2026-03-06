package com.darkzoom.tempsphere.data.repository

import android.util.Log
import com.darkzoom.androidwithkotlin.BuildConfig
import com.darkzoom.tempsphere.data.local.datasource.WeatherLocalDatasource
import com.darkzoom.tempsphere.data.local.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.data.local.entity.ForecastItemEntity
import com.darkzoom.tempsphere.data.remote.datasource.WeatherRemoteDatasource
import com.darkzoom.tempsphere.utils.toEntities
import com.darkzoom.tempsphere.utils.toEntity
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val remoteDataSource: WeatherRemoteDatasource,
    private val localDatasource: WeatherLocalDatasource
) {
    private val apiKey = BuildConfig.API_KEY

    fun getCurrentWeather(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<CurrentWeatherEntity?> {
        return localDatasource.getCurrentWeather(lat, lon, units, lang)
    }

    suspend fun refreshCurrentWeather(
        lat: Double, lon: Double, units: String, lang: String
    ): Result<Unit> {
        return try {
            android.util.Log.d("API_KEY_TEST", "My key from Gradle is: ->$apiKey<-")
            val response = remoteDataSource.getCurrentWeather(lat, lon, apiKey, units, lang)
            localDatasource.cacheCurrentWeather(response.toEntity(units, lang))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun getForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Flow<List<ForecastItemEntity>> {
        return localDatasource.getForecast(lat, lon, units, lang)
    }

    suspend fun refreshForecast(
        lat: Double, lon: Double, units: String, lang: String
    ): Result<Unit> {
        return try {
            val response = remoteDataSource.getForecast(lat, lon, apiKey, units, lang)
            localDatasource.clearForecast(lat, lon, units, lang)
            localDatasource.cacheForecast(response.toEntities(units, lang))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}