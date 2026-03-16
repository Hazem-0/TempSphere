package com.darkzoom.tempsphere.data.local.datasource

import CurrentWeatherEntity
import ForecastItemEntity
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.darkzoom.tempsphere.data.contract.WeatherLocalDatasource
import com.darkzoom.tempsphere.data.local.db.WeatherDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherLocalDatasourceTest {

    private lateinit var database: WeatherDatabase
    private lateinit var localDatasource: WeatherLocalDatasource

    private val sampleWeather = CurrentWeatherEntity(
        id = 1, lat = 0.0, lon = 0.0, cityName = "Cairo", country = "",
        temp = 25.0, feelsLike = 0.0, tempMin = 0.0, tempMax = 0.0,
        pressure = 0, humidity = 0, visibility = 0, windSpeed = 0.0,
        windDeg = 0, windGust = null, cloudsAll = 0, weatherMain = "",
        weatherDescription = "", weatherIcon = "", sunrise = 0L, sunset = 0L,
        dt = 0L, timezone = 0, units = "", lang = ""
    )

    private val sampleForecastItems = listOf(
        createDummyForecast(dt = 1L),
        createDummyForecast(dt = 2L)
    )

    private fun createDummyForecast(dt: Long, desc: String = "") = ForecastItemEntity(
        dt = dt, cityId = 0, cityName = "", country = "", cityLat = 0.0, cityLon = 0.0,
        cityTimezone = 0, citySunrise = 0L, citySunset = 0L, temp = 0.0, feelsLike = 0.0,
        tempMin = 0.0, tempMax = 0.0, pressure = 0, humidity = 0, visibility = 0,
        windSpeed = 0.0, windDeg = 0, windGust = null, cloudsAll = 0, weatherMain = "",
        weatherDescription = desc, weatherIcon = "", pop = 0.0, dtTxt = "", units = "", lang = ""
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        localDatasource = WeatherLocalDatasourceImp(
            currentWeatherDao = database.currentWeatherDao(),
            forecastDao = database.forecastDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun cacheCurrentWeather_thenGetCurrentWeather_returnsCorrectData() = runTest {
        localDatasource.cacheCurrentWeather(sampleWeather)

        val result = localDatasource.getCurrentWeather().first()

        assertNotNull(result)
        assertEquals("Cairo", result!!.cityName)
        assertEquals(25.0, result.temp, 0.001)
    }

    @Test
    fun clearAllCurrentWeather_thenGet_returnsNull() = runTest {
        localDatasource.cacheCurrentWeather(sampleWeather)
        localDatasource.clearAllCurrentWeather()

        val result = localDatasource.getCurrentWeather().first()

        assertNull(result)
    }

    @Test
    fun cacheForecast_thenGetForecast_returnsAllItems() = runTest {
        localDatasource.cacheForecast(sampleForecastItems)

        val result = localDatasource.getForecast().first()

        assertEquals(2, result.size)
    }

    @Test
    fun clearAllForecast_thenGetForecast_returnsEmptyList() = runTest {
        localDatasource.cacheForecast(sampleForecastItems)
        localDatasource.clearAllForecast()

        val result = localDatasource.getForecast().first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun cacheForecast_replaceWithNewItems_returnsNewItems() = runTest {
        localDatasource.cacheForecast(sampleForecastItems)
        localDatasource.clearAllForecast()

        val newItem = createDummyForecast(dt = 3L, desc = "light rain")
        localDatasource.cacheForecast(listOf(newItem))

        val result = localDatasource.getForecast().first()

        assertEquals(1, result.size)
        assertEquals("light rain", result[0].weatherDescription)
    }
}