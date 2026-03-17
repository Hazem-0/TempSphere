package com.darkzoom.tempsphere.data.contract

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val locationModeFlow: StateFlow<String>
    var locationMode: String

    val tempUnitFlow: StateFlow<String>
    var tempUnit: String

    val windUnitFlow: StateFlow<String>
    var windUnit: String

    val languageFlow: StateFlow<String>
    var language: String

    var dataRefreshRate: String

    var mapLat: Double?
    var mapLon: Double?

    fun getMapLocation(): Pair<Double, Double>?
    fun saveMapLocation(lat: Double, lon: Double)
}