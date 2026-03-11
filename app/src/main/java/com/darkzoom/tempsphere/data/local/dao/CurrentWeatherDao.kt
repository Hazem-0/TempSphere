package com.darkzoom.tempsphere.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darkzoom.tempsphere.data.local.entity.CurrentWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(entity: CurrentWeatherEntity)

    @Query("SELECT * FROM current_weather LIMIT 1")
    fun getCurrentWeather(): Flow<CurrentWeatherEntity?>

    @Query("""DELETE FROM current_weather
        WHERE lat = :lat AND lon = :lon AND units = :units AND lang = :lang
        """)
    suspend fun deleteCurrentWeather(lat: Double, lon: Double, units: String, lang: String)

    @Query("DELETE FROM current_weather")
    suspend fun deleteAllCurrentWeather()
}