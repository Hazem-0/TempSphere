package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.contract.SharedPrefDatasource

class SettingsRepository(val sharedPrefs: SharedPrefDatasource) {

    companion object {
        const val KEY_LOCATION_MODE = "location_mode"
        const val KEY_TEMP_UNIT = "temp_unit"
        const val KEY_WIND_UNIT = "wind_unit"
        const val KEY_LANGUAGE = "language"
        const val KEY_THEME = "theme"
        const val KEY_NOTIFICATIONS = "notifications"
        const val KEY_DATA_REFRESH = "data_refresh"

        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun getInstance(sharedPrefs: SharedPrefDatasource): SettingsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(sharedPrefs).also { INSTANCE = it }
            }
        }
    }

    var locationMode: String
        get() = sharedPrefs.getString(KEY_LOCATION_MODE, "GPS")
        set(value) = sharedPrefs.putString(KEY_LOCATION_MODE, value)

    var tempUnit: String
        get() = sharedPrefs.getString(KEY_TEMP_UNIT, "Fahrenheit")
        set(value) = sharedPrefs.putString(KEY_TEMP_UNIT, value)

    var windUnit: String
        get() = sharedPrefs.getString(KEY_WIND_UNIT, "m/s")
        set(value) = sharedPrefs.putString(KEY_WIND_UNIT, value)

    var language: String
        get() = sharedPrefs.getString(KEY_LANGUAGE, "English")
        set(value) = sharedPrefs.putString(KEY_LANGUAGE, value)

    var theme: String
        get() = sharedPrefs.getString(KEY_THEME, "Starry Night")
        set(value) = sharedPrefs.putString(KEY_THEME, value)

    var notificationsEnabled: Boolean
        get() = sharedPrefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = sharedPrefs.putBoolean(KEY_NOTIFICATIONS, value)

    var dataRefreshRate: String
        get() = sharedPrefs.getString(KEY_DATA_REFRESH, "30 min")
        set(value) = sharedPrefs.putString(KEY_DATA_REFRESH, value)
}