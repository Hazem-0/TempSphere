package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.contract.SettingsRepository
import com.darkzoom.tempsphere.data.contract.SharedPrefDatasource

class SettingsRepositoryImp(val sharedPrefs: SharedPrefDatasource) : SettingsRepository {

    companion object {
        const val KEY_LOCATION_MODE = "location_mode"
        const val KEY_TEMP_UNIT = "temp_unit"
        const val KEY_WIND_UNIT = "wind_unit"
        const val KEY_LANGUAGE = "language"
        const val KEY_DATA_REFRESH = "data_refresh"

        @Volatile
        private var INSTANCE: SettingsRepositoryImp? = null

        fun getInstance(sharedPrefs: SharedPrefDatasource): SettingsRepositoryImp {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepositoryImp(sharedPrefs).also { INSTANCE = it }
            }
        }
    }

    override var locationMode: String
        get() = sharedPrefs.getString(KEY_LOCATION_MODE, "GPS")
        set(value) = sharedPrefs.putString(KEY_LOCATION_MODE, value)

    override var tempUnit: String
        get() = sharedPrefs.getString(KEY_TEMP_UNIT, "Fahrenheit")
        set(value) = sharedPrefs.putString(KEY_TEMP_UNIT, value)

    override var windUnit: String
        get() = sharedPrefs.getString(KEY_WIND_UNIT, "m/s")
        set(value) = sharedPrefs.putString(KEY_WIND_UNIT, value)

    override var language: String
        get() = sharedPrefs.getString(KEY_LANGUAGE, "English")
        set(value) = sharedPrefs.putString(KEY_LANGUAGE, value)

    override var dataRefreshRate: String
        get() = sharedPrefs.getString(KEY_DATA_REFRESH, "30 min")
        set(value) = sharedPrefs.putString(KEY_DATA_REFRESH, value)
}