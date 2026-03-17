package com.darkzoom.tempsphere.ui.core

import android.app.Application
import android.content.Context
import com.darkzoom.tempsphere.data.contract.SharedPrefDatasource
import com.darkzoom.tempsphere.data.local.datasource.AlertLocalDatasourceImp
import com.darkzoom.tempsphere.data.local.datasource.FavouriteLocationDatasourceImp
import com.darkzoom.tempsphere.data.local.datasource.SharedPrefDatasourceImp
import com.darkzoom.tempsphere.data.local.datasource.WeatherLocalDatasourceImp
import com.darkzoom.tempsphere.data.local.db.WeatherDatabase
import com.darkzoom.tempsphere.data.remote.datasource.WeatherRemoteDatasourceImp
import com.darkzoom.tempsphere.data.repository.AlertRepositoryImp
import com.darkzoom.tempsphere.data.repository.SettingsRepositoryImp
import com.darkzoom.tempsphere.data.repository.WeatherRepositoryImp
import com.darkzoom.tempsphere.utils.AlertManager
import com.darkzoom.tempsphere.utils.LocaleHelper
import com.darkzoom.tempsphere.utils.LocationUtilImp
import com.google.android.gms.location.LocationServices

class App : Application() {

    private val db by lazy { WeatherDatabase.getInstance(this) }

    val locationTracker by lazy {
        LocationUtilImp(
            LocationServices.getFusedLocationProviderClient(this),
            this
        )
    }

    private val weatherLocalDatasource by lazy {
        WeatherLocalDatasourceImp(db.currentWeatherDao(), db.forecastDao())
    }

    private val weatherRemoteDatasource by lazy { WeatherRemoteDatasourceImp() }

    private val favouriteLocalDatasource by lazy {
        FavouriteLocationDatasourceImp(db.favouriteLocationDao())
    }

    val repository by lazy {
        WeatherRepositoryImp(
            remoteDataSource         = weatherRemoteDatasource,
            localDatasource          = weatherLocalDatasource,
            favouriteLocalDatasource = favouriteLocalDatasource
        )
    }

    private val sharedPrefDatasource: SharedPrefDatasource by lazy {
        SharedPrefDatasourceImp(
            getSharedPreferences("tempsphere_preferences", Context.MODE_PRIVATE)
        )
    }

    val settingsRepository by lazy {
        SettingsRepositoryImp.getInstance(sharedPrefDatasource)
    }

    val alertManager by lazy { AlertManager(this) }

    private val alertLocalDatasource by lazy {
        AlertLocalDatasourceImp(db.alertDao())
    }

    val alertRepository by lazy {
        AlertRepositoryImp(alertLocalDatasource, alertManager)
    }


    override fun attachBaseContext(base: Context) {

        val prefs    = base.getSharedPreferences("tempsphere_preferences", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "English") ?: "English"
        super.attachBaseContext(LocaleHelper.wrap(base, language))
    }
}