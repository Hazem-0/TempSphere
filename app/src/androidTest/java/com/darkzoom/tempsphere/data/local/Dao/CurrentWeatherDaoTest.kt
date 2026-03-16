package com.darkzoom.tempsphere.data.local.dao

import CurrentWeatherEntity
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.darkzoom.tempsphere.data.local.db.WeatherDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrentWeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: CurrentWeatherDao

    private val sampleEntity = CurrentWeatherEntity(
        id = 1, lat = 0.0, lon = 0.0, cityName = "Cairo", country = "",
        temp = 25.0, feelsLike = 0.0, tempMin = 0.0, tempMax = 0.0,
        pressure = 0, humidity = 0, visibility = 0, windSpeed = 0.0,
        windDeg = 0, windGust = null, cloudsAll = 0, weatherMain = "",
        weatherDescription = "", weatherIcon = "", sunrise = 0L, sunset = 0L,
        dt = 0L, timezone = 0, units = "", lang = ""
    )

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        dao = database.currentWeatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertCurrentWeather_getFromDb_returnsInsertedEntity() = runTest {
        dao.insertCurrentWeather(sampleEntity)

        val result = dao.getCurrentWeather().first()

        assertNotNull(result)
        assertEquals("Cairo", result!!.cityName)
        assertEquals(25.0, result.temp, 0.001)
    }

    @Test
    fun deleteAllCurrentWeather_thenGet_returnsNull() = runTest {
        dao.insertCurrentWeather(sampleEntity)
        dao.deleteAllCurrentWeather()

        val result = dao.getCurrentWeather().first()

        assertNull(result)
    }

    @Test
    fun insertCurrentWeather_replace_returnsLatestEntity() = runTest {
        dao.insertCurrentWeather(sampleEntity)

        val updatedEntity = sampleEntity.copy(cityName = "Alexandria", temp = 28.0)
        dao.insertCurrentWeather(updatedEntity)

        val result = dao.getCurrentWeather().first()

        assertNotNull(result)
        assertEquals("Alexandria", result!!.cityName)
        assertEquals(28.0, result.temp, 0.001)
    }
}