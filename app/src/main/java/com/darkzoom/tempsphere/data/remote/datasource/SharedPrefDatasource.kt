package com.darkzoom.tempsphere.data.remote.datasource

import android.content.Context
import android.content.SharedPreferences

class SharedPrefDatasource private constructor(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        "tempsphere_settings",
        Context.MODE_PRIVATE
    )

    fun getString(key: String, defaultValue: String): String = prefs.getString(key, defaultValue) ?: defaultValue
    fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    fun getBoolean(key: String, defaultValue: Boolean): Boolean = prefs.getBoolean(key, defaultValue)
    fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()

    companion object {
        @Volatile
        private var INSTANCE: SharedPrefDatasource? = null

        fun getInstance(context: Context): SharedPrefDatasource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefDatasource(context).also { INSTANCE = it }
            }
        }
    }
}