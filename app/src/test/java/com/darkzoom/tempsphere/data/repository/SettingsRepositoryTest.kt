package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.fake.FakeSharedPrefDatasource
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class SettingsRepositoryTest {

    private lateinit var fakeSharedPrefs: FakeSharedPrefDatasource
    private lateinit var settingsRepository: SettingsRepositoryImp

    @Before
    fun setup() {
        fakeSharedPrefs = FakeSharedPrefDatasource()
        settingsRepository = SettingsRepositoryImp(fakeSharedPrefs)
    }

    @Test
    fun locationMode_getLocation_returnsDefaultValue() {
        val result = settingsRepository.locationMode

        assertEquals("GPS", result)
    }

    @Test
    fun locationMode_setLocation_savesValueToSharedPrefs() {
        val newMode = "Manual"
        settingsRepository.locationMode = newMode
        assertEquals(newMode, settingsRepository.locationMode)
        assertEquals(newMode, fakeSharedPrefs.getString(SettingsRepositoryImp.KEY_LOCATION_MODE, ""))
    }

    @Test
    fun tempUnit_getTempUnit_returnsDefaultValue() {
        val result = settingsRepository.tempUnit
        assertEquals("Fahrenheit", result)
    }

    @Test
    fun tempUnit_setTempUnit_savesValueToSharedPrefs() {
        val newUnit = "Celsius"
        settingsRepository.tempUnit = newUnit
        assertEquals(newUnit, settingsRepository.tempUnit)
        assertEquals(newUnit, fakeSharedPrefs.getString(SettingsRepositoryImp.KEY_TEMP_UNIT, ""))
    }
}