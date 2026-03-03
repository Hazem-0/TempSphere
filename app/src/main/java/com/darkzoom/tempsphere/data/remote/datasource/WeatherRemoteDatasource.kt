package com.darkzoom.tempsphere.data.remote.datasource

import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse
import com.darkzoom.tempsphere.data.remote.network.RetrofitClient

class WeatherRemoteDatasource {
    private val apiService = RetrofitClient.api

    suspend fun getCurrentWeather(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): CurrentWeatherResponse {
        return apiService.getCurrentWeather(lat, lon, apiKey, units, lang)
    }

    suspend fun getForecast(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): ForecastResponse {
        return apiService.getForecast(lat, lon, apiKey, units, lang)
    }
}