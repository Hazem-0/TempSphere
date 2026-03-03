package com.darkzoom.tempsphere.data.repository

import com.darkzoom.androidwithkotlin.BuildConfig
import com.darkzoom.tempsphere.data.remote.datasource.WeatherRemoteDatasource
import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse
import com.darkzoom.tempsphere.domain.repository.WeatherRepository

class WeatherRepositoryImp(
    private val remoteDataSource: WeatherRemoteDatasource
) : WeatherRepository {

    private val apiKey = BuildConfig.API_KEY

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<CurrentWeatherResponse> {
        return try {
            val response = remoteDataSource.getCurrentWeather(lat, lon, apiKey, units, lang)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<ForecastResponse> {
        return try {
            val response = remoteDataSource.getForecast(lat, lon, apiKey, units, lang)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}