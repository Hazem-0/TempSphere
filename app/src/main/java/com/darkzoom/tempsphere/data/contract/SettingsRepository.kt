package com.darkzoom.tempsphere.data.contract

interface SettingsRepository {
    var locationMode: String
    var tempUnit: String
    var windUnit: String
    var language: String
    var dataRefreshRate: String
}