package com.darkzoom.tempsphere.data.contract

import CurrentWeatherEntity
import ForecastItemEntity
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherEntity?>

    suspend fun refreshCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<Unit>

    suspend fun clearCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    )

    suspend fun clearAllCurrentWeather()

    suspend fun clearAllForecast()

    fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<List<ForecastItemEntity>>

    suspend fun refreshForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<Unit>

    fun getAllFavourites(): Flow<List<FavLocationEntity>>

    suspend fun getFavouriteById(id: Int): FavLocationEntity?

    suspend fun addFavourite(
        city: String,
        country: String,
        lat: Double,
        lon: Double
    ): Result<Long>

    suspend fun removeFavourite(id: Int): Result<Unit>

    suspend fun updateFavourite(entity: FavLocationEntity): Result<Unit>

    suspend fun isFavourite(lat: Double, lon: Double): Boolean

    suspend fun getCurrentWeatherForLocation(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<CurrentWeatherResponse>

    suspend fun getForecastForLocation(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<ForecastResponse>

    suspend fun resolveLocationName(
        lat: Double,
        lon: Double
    ): Result<Pair<String, String>>
}