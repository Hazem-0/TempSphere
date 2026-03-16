package com.darkzoom.tempsphere.data.contract

import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse

interface WeatherRemoteDatasource {
    suspend fun getCurrentWeather(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): CurrentWeatherResponse

    suspend fun getForecast(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): ForecastResponse
}