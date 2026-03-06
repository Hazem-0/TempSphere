package com.darkzoom.tempsphere.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.darkzoom.tempsphere.data.local.dao.CurrentWeatherDao
import com.darkzoom.tempsphere.data.local.dao.ForecastDao
import com.darkzoom.tempsphere.data.local.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.data.local.entity.ForecastItemEntity

@Database(entities = [CurrentWeatherEntity::class, ForecastItemEntity::class], version = 1)

abstract class WeatherDatabase : RoomDatabase() {

    abstract fun currentWeatherDao(): CurrentWeatherDao
    abstract fun forecastDao(): ForecastDao

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