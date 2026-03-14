package com.darkzoom.tempsphere.data.local.dao

import ForecastItemEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastItems(items: List<ForecastItemEntity>)

    @Query("SELECT * FROM forecast_items ORDER BY dt ASC")
    fun getForecast(): Flow<List<ForecastItemEntity>>

    @Query("""DELETE FROM forecast_items
        WHERE cityLat = :lat AND cityLon = :lon AND units = :units AND lang = :lang""")
    suspend fun deleteForecast(lat: Double, lon: Double, units: String, lang: String)


    @Query("DELETE FROM forecast_items")
    suspend fun deleteAllForecast()

}