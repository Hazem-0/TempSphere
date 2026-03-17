package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.contract.SettingsRepository
import com.darkzoom.tempsphere.data.contract.SharedPrefDatasource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImp(val sharedPrefs: SharedPrefDatasource) : SettingsRepository {

    companion object {
        const val KEY_LOCATION_MODE  = "location_mode"
        const val KEY_TEMP_UNIT      = "temp_unit"
        const val KEY_WIND_UNIT      = "wind_unit"
        const val KEY_LANGUAGE       = "language"
        const val KEY_DATA_REFRESH   = "data_refresh"
        const val KEY_MAP_LAT        = "map_lat"
        const val KEY_MAP_LON        = "map_lon"

        @Volatile
        private var INSTANCE: SettingsRepositoryImp? = null

        fun getInstance(sharedPrefs: SharedPrefDatasource): SettingsRepositoryImp =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepositoryImp(sharedPrefs).also { INSTANCE = it }
            }
    }

    private val _locationModeFlow = MutableStateFlow(
        sharedPrefs.getString(KEY_LOCATION_MODE, "GPS")
    )
    override val locationModeFlow: StateFlow<String> = _locationModeFlow.asStateFlow()

    override var locationMode: String
        get() = sharedPrefs.getString(KEY_LOCATION_MODE, "GPS")
        set(value) {
            sharedPrefs.putString(KEY_LOCATION_MODE, value)
            _locationModeFlow.value = value
        }

    private val _tempUnitFlow = MutableStateFlow(
        sharedPrefs.getString(KEY_TEMP_UNIT, "Fahrenheit")
    )
    override val tempUnitFlow: StateFlow<String> = _tempUnitFlow.asStateFlow()

    override var tempUnit: String
        get() = sharedPrefs.getString(KEY_TEMP_UNIT, "Fahrenheit")
        set(value) {
            sharedPrefs.putString(KEY_TEMP_UNIT, value)
            _tempUnitFlow.value = value
        }

    private val _windUnitFlow = MutableStateFlow(
        sharedPrefs.getString(KEY_WIND_UNIT, "m/s")
    )
    override val windUnitFlow: StateFlow<String> = _windUnitFlow.asStateFlow()

    override var windUnit: String
        get() = sharedPrefs.getString(KEY_WIND_UNIT, "m/s")
        set(value) {
            sharedPrefs.putString(KEY_WIND_UNIT, value)
            _windUnitFlow.value = value
        }

    private val _languageFlow = MutableStateFlow(
        sharedPrefs.getString(KEY_LANGUAGE, "English")
    )
    override val languageFlow: StateFlow<String> = _languageFlow.asStateFlow()

    override var language: String
        get() = sharedPrefs.getString(KEY_LANGUAGE, "English")
        set(value) {
            sharedPrefs.putString(KEY_LANGUAGE, value)
            _languageFlow.value = value
        }

    override var dataRefreshRate: String
        get() = sharedPrefs.getString(KEY_DATA_REFRESH, "30 min")
        set(value) = sharedPrefs.putString(KEY_DATA_REFRESH, value)

    override var mapLat: Double?
        get() {
            val raw = sharedPrefs.getString(KEY_MAP_LAT, "")
            return if (raw.isNullOrEmpty()) null else raw.toDoubleOrNull()
        }
        set(value) = sharedPrefs.putString(KEY_MAP_LAT, value?.toString() ?: "")

    override var mapLon: Double?
        get() {
            val raw = sharedPrefs.getString(KEY_MAP_LON, "")
            return if (raw.isNullOrEmpty()) null else raw.toDoubleOrNull()
        }
        set(value) = sharedPrefs.putString(KEY_MAP_LON, value?.toString() ?: "")

    override fun getMapLocation(): Pair<Double, Double>? {
        val lat = mapLat ?: return null
        val lon = mapLon ?: return null
        return Pair(lat, lon)
    }

    override fun saveMapLocation(lat: Double, lon: Double) {
        mapLat = lat
        mapLon = lon
    }
}