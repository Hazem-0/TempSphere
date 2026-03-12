package com.darkzoom.tempsphere.data.local.datasource

import android.content.SharedPreferences
import com.darkzoom.tempsphere.data.contract.SharedPrefDatasource

class SharedPrefDatasourceImp(
    private val prefs: SharedPreferences
) : SharedPrefDatasource {

    override fun getString(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}