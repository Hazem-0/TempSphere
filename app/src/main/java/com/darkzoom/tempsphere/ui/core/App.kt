package com.darkzoom.tempsphere.ui.core

import android.app.Application
import com.darkzoom.tempsphere.data.local.database.WeatherDatabase
import com.darkzoom.tempsphere.data.local.datasource.WeatherLocalDatasource
import com.darkzoom.tempsphere.data.remote.datasource.WeatherRemoteDatasource
import com.darkzoom.tempsphere.data.repository.WeatherRepository
import com.darkzoom.tempsphere.utils.LocationUtil // <-- Correctly imported from utils!
import com.google.android.gms.location.LocationServices

class App : Application() {

    private val db by lazy { WeatherDatabase.getInstance(this) }

    val locationTracker by lazy {
        LocationUtil(
            LocationServices.getFusedLocationProviderClient(this),
            this
        )
    }

    private val localDatasource by lazy {
        WeatherLocalDatasource(
            db.currentWeatherDao(),
            db.forecastDao()
        )
    }

    private val remoteDatasource by lazy { WeatherRemoteDatasource() }

    val repository by lazy {
        WeatherRepository(remoteDatasource, localDatasource)
    }
}