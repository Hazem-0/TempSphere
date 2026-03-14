package com.darkzoom.tempsphere.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.local.model.HomeUiState
import com.darkzoom.tempsphere.data.repository.SettingsRepository
import com.darkzoom.tempsphere.data.repository.WeatherRepository
import com.darkzoom.tempsphere.utils.LocationUtil
import com.darkzoom.tempsphere.utils.toSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WeatherRepository,
    private val locationTracker: LocationUtil,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val apiUnits: String
        get() = when (settingsRepository.tempUnit) {
            "Celsius" -> "metric"
            "Fahrenheit" -> "imperial"
            else -> "standard"
        }

    private val apiLang: String
        get() = when (settingsRepository.language) {
            "Arabic" -> "ar"
            else -> "en"
        }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        observeWeather()
    }

    private fun observeWeather() {
        viewModelScope.launch {

            combine(
                repository.getCurrentWeather(0.0, 0.0, apiUnits, apiLang),
                repository.getForecast(0.0, 0.0, apiUnits, apiLang)
            ) { current, forecast ->
                if (current != null && forecast.isNotEmpty()) {
                    current.toSuccess(forecast)
                } else {
                    HomeUiState.Loading
                }
            }.collect { newState ->
                val refreshing = (_uiState.value as? HomeUiState.Success)?.isRefreshing ?: false
                _uiState.value = if (newState is HomeUiState.Success)
                    newState.copy(isRefreshing = refreshing)
                else newState
            }
        }
    }

    fun loadWeather(context: android.content.Context) {
        viewModelScope.launch {
            _uiState.update { if (it is HomeUiState.Success) it.copy(isRefreshing = true) else HomeUiState.Loading }

            val location = locationTracker.getCurrentLocation()

            if (location != null) {
                val (lat, lon) = location

                val current = repository.refreshCurrentWeather(lat, lon, apiUnits, apiLang)
                val forecast = repository.refreshForecast(lat, lon, apiUnits, apiLang)

                if (current.isFailure || forecast.isFailure) {
                    val msg = (current.exceptionOrNull() ?: forecast.exceptionOrNull())?.message
                        ?: context.getString(R.string.failed_to_fetch_weather)

                    if (_uiState.value is HomeUiState.Loading) _uiState.value = HomeUiState.Error(msg)
                }
                _uiState.update { if (it is HomeUiState.Success) it.copy(isRefreshing = false) else it }
            } else {
                _uiState.value = HomeUiState.Error(context.getString(R.string.location_permission_required_or_gps_disabled))
            }
        }
    }

    class Factory(
        private val repository: WeatherRepository,
        private val locationTracker: LocationUtil,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository, locationTracker, settingsRepository) as T
        }
    }
}