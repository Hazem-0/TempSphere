package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.BuildConfig
import com.darkzoom.tempsphere.data.local.datasource.FavouriteLocalDatasource
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import com.darkzoom.tempsphere.data.remote.datasource.WeatherRemoteDatasource
import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

class FavouritesRepository(
    private val localDatasource: FavouriteLocalDatasource,
    private val remoteDatasource: WeatherRemoteDatasource
) {
    private val apiKey = BuildConfig.API_KEY


    fun getAllFavourites(): Flow<List<FavLocationEntity>> =
        localDatasource.getAllFavourites()

    suspend fun getFavouriteById(id: Int): FavLocationEntity? =
        localDatasource.getFavouriteById(id)

    suspend fun addFavourite(
        city: String,
        country: String,
        lat: Double,
        lon: Double
    ): Result<Long> = runCatching {
        localDatasource.insertFavourite(
            FavLocationEntity(
                city = city,
                country = country,
                latitude = lat,
                longitude = lon
            )
        )
    }

    suspend fun removeFavourite(id: Int): Result<Unit> = runCatching {
        localDatasource.deleteFavouriteById(id)
    }

    suspend fun isFavourite(lat: Double, lon: Double): Boolean =
        localDatasource.isFavourite(lat, lon)


    suspend fun getCurrentWeatherForLocation(
        lat: Double,
        lon: Double,
        units: String = "metric",
        lang: String = "en"
    ): Result<CurrentWeatherResponse> = runCatching {
        remoteDatasource.getCurrentWeather(lat, lon, apiKey, units, lang)
    }

    suspend fun getForecastForLocation(
        lat: Double,
        lon: Double,
        units: String = "metric",
        lang: String = "en"
    ): Result<ForecastResponse> = runCatching {
        remoteDatasource.getForecast(lat, lon, apiKey, units, lang)
    }



    suspend fun resolveLocationName(
        lat: Double,
        lon: Double
    ): Result<Pair<String, String>> = runCatching {
        val response = remoteDatasource.getCurrentWeather(lat, lon, apiKey, "metric", "en")
        Pair(response.name ?: "Unknown", response.sys?.country ?: "")
    }
}