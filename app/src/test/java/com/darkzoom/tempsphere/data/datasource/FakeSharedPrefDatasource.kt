package com.darkzoom.tempsphere.data.fake

import com.darkzoom.tempsphere.data.contract.SharedPrefDatasource

class FakeSharedPrefDatasource : SharedPrefDatasource {

    private val stringStorage = mutableMapOf<String, String>()
    private val booleanStorage = mutableMapOf<String, Boolean>()
    override fun getString(key: String, defaultValue: String): String {
        return stringStorage[key] ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        stringStorage[key] = value
    }
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return booleanStorage[key] ?: defaultValue
    }
    override fun putBoolean(key: String, value: Boolean) {
        booleanStorage[key] = value
    }
}