package com.darkzoom.tempsphere.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.darkzoom.tempsphere.data.local.dao.AlertDao
import com.darkzoom.tempsphere.data.local.dao.CurrentWeatherDao
import com.darkzoom.tempsphere.data.local.dao.ForecastDao
import com.darkzoom.tempsphere.data.local.model.entity.AlertEntity

@Database(
    entities = [
        CurrentWeatherEntity::class,
        ForecastItemEntity::class,
        AlertEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun forecastDao(): ForecastDao
    abstract fun alertDao(): AlertDao

    companion object {
        private const val DATABASE_NAME = "weather_db"

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}