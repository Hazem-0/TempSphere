package com.darkzoom.tempsphere.data.repository

import CurrentWeatherEntity
import ForecastItemEntity
import com.darkzoom.tempsphere.BuildConfig
import com.darkzoom.tempsphere.data.contract.FavouriteLocationDatasource
import com.darkzoom.tempsphere.data.contract.WeatherLocalDatasource
import com.darkzoom.tempsphere.data.contract.WeatherRepository
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import com.darkzoom.tempsphere.data.remote.datasource.WeatherRemoteDatasource
import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse
import com.darkzoom.tempsphere.utils.toEntities
import com.darkzoom.tempsphere.utils.toEntity
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImp(
    private val remoteDataSource: WeatherRemoteDatasource,
    private val localDatasource: WeatherLocalDatasource,
    private val favouriteLocalDatasource: FavouriteLocationDatasource
) : WeatherRepository {

    private val apiKey = BuildConfig.API_KEY

    override fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<CurrentWeatherEntity?> {
        return localDatasource.getCurrentWeather()
    }

    override suspend fun refreshCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<Unit> = runCatching {
        val response = remoteDataSource.getCurrentWeather(lat, lon, apiKey, units, lang)
        localDatasource.clearAllCurrentWeather()
        localDatasource.cacheCurrentWeather(response.toEntity(units, lang))
    }

    override suspend fun clearCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ) {
        localDatasource.clearCurrentWeather(lat, lon, units, lang)
    }

    override suspend fun clearAllCurrentWeather() {
        localDatasource.clearAllCurrentWeather()
    }

    override suspend fun clearAllForecast() {
        localDatasource.clearAllForecast()
    }

    override fun getForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<List<ForecastItemEntity>> {
        return localDatasource.getForecast()
    }

    override suspend fun refreshForecast(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<Unit> = runCatching {
        val response = remoteDataSource.getForecast(lat, lon, apiKey, units, lang)
        localDatasource.clearAllForecast()
        localDatasource.cacheForecast(response.toEntities(units, lang))
    }

    override fun getAllFavourites(): Flow<List<FavLocationEntity>> =
        favouriteLocalDatasource.getAllFavourites()

    override suspend fun getFavouriteById(id: Int): FavLocationEntity? =
        favouriteLocalDatasource.getFavouriteById(id)

    override suspend fun addFavourite(
        city: String,
        country: String,
        lat: Double,
        lon: Double
    ): Result<Long> = runCatching {
        favouriteLocalDatasource.insertFavourite(
            FavLocationEntity(
                city = city,
                country = country,
                latitude = lat,
                longitude = lon
            )
        )
    }

    override suspend fun removeFavourite(id: Int): Result<Unit> =
        runCatching { favouriteLocalDatasource.deleteFavouriteById(id) }

    override suspend fun updateFavourite(entity: FavLocationEntity): Result<Unit> =
        runCatching { favouriteLocalDatasource.updateFavourite(entity) }

    override suspend fun isFavourite(lat: Double, lon: Double): Boolean =
        favouriteLocalDatasource.isFavourite(lat, lon)

    override suspend fun getCurrentWeatherForLocation(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<CurrentWeatherResponse> = runCatching {
        remoteDataSource.getCurrentWeather(lat, lon, apiKey, units, lang)
    }

    override suspend fun getForecastForLocation(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Result<ForecastResponse> = runCatching {
        remoteDataSource.getForecast(lat, lon, apiKey, units, lang)
    }

    override suspend fun resolveLocationName(
        lat: Double,
        lon: Double
    ): Result<Pair<String, String>> = runCatching {
        val response = remoteDataSource.getCurrentWeather(lat, lon, apiKey, "metric", "en")
        Pair(response.name ?: "Unknown", response.sys?.country ?: "")
    }
}