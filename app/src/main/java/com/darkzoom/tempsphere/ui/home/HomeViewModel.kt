package com.darkzoom.tempsphere.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.local.model.DailyWeather
import com.darkzoom.tempsphere.data.local.model.HourlyWeather
import com.darkzoom.tempsphere.data.local.model.WeatherType
import com.darkzoom.tempsphere.data.repository.SettingsRepositoryImp
import com.darkzoom.tempsphere.data.repository.WeatherRepositoryImp
import com.darkzoom.tempsphere.utils.LocationUtil
import com.darkzoom.tempsphere.utils.toSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data object Offline : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    data class Success(
        val city          : String,
        val temp          : Int,
        val feelsLike     : Int,
        val high          : Int,
        val low           : Int,
        val description   : String,
        val weatherType   : WeatherType,
        val humidity      : Int,
        val windMs        : Float,
        val pressureHpa   : Int,
        val cloudinessPct : Int,
        val dateLabel     : String,
        val hourly        : List<HourlyWeather>,
        val daily         : List<DailyWeather>,
        val isRefreshing  : Boolean = false
    ) : HomeUiState()
}

class HomeViewModel(
    private val repository: WeatherRepositoryImp,
    private val locationTracker: LocationUtil,
    private val settingsRepository: SettingsRepositoryImp
) : ViewModel() {

    private val apiUnits: String
        get() = when (settingsRepository.tempUnit) {
            "Celsius"    -> "metric"
            "Fahrenheit" -> "imperial"
            else         -> "standard"
        }

    private val apiLang: String
        get() = when (settingsRepository.language) {
            "Arabic" -> "ar"
            else     -> "en"
        }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    fun observeWeather(context: Context) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val location = locationTracker.getCurrentLocation()
            if (location == null) {
                _uiState.value = HomeUiState.Error(
                    context.getString(R.string.location_permission_required_or_gps_disabled)
                )
                return@launch
            }

            val (lat, lon) = location

            launch {
                combine(
                    repository.getCurrentWeather(lat, lon, apiUnits, apiLang),
                    repository.getForecast(lat, lon, apiUnits, apiLang)
                ) { current, forecast ->
                    if (current != null && forecast.isNotEmpty()) current.toSuccess(forecast)
                    else null
                }.collect { state ->
                    if (state != null) {
                        val isRefreshing = (_uiState.value as? HomeUiState.Success)?.isRefreshing ?: false
                        _uiState.value = state.copy(isRefreshing = isRefreshing)
                    }
                }
            }

            if (isOnline(context)) {
                val weatherResult = repository.refreshCurrentWeather(lat, lon, apiUnits, apiLang)
                repository.refreshForecast(lat, lon, apiUnits, apiLang)

                val hasCachedWeather = repository.getCurrentWeather(lat, lon, apiUnits, apiLang).first() != null
                if (!hasCachedWeather && weatherResult.isFailure) {
                    _uiState.value = HomeUiState.Error(
                        weatherResult.exceptionOrNull()?.message ?: "Failed to fetch weather data."
                    )
                }
            } else {
                val hasCachedWeather  = repository.getCurrentWeather(lat, lon, apiUnits, apiLang).first() != null
                val hasCachedForecast = repository.getForecast(lat, lon, apiUnits, apiLang).first().isNotEmpty()

                if (!hasCachedWeather || !hasCachedForecast) {
                    _uiState.value = HomeUiState.Offline
                }
            }
        }
    }

    fun refresh(context: Context) {
        viewModelScope.launch {
            val location = locationTracker.getCurrentLocation() ?: return@launch
            val (lat, lon) = location

            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            }

            if (isOnline(context)) {
                repository.refreshCurrentWeather(lat, lon, apiUnits, apiLang)
                repository.refreshForecast(lat, lon, apiUnits, apiLang)

                val postRefreshState = _uiState.value
                if (postRefreshState is HomeUiState.Success) {
                    _uiState.value = postRefreshState.copy(isRefreshing = false)
                }
            } else {
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(isRefreshing = false)
                }

                val hasCachedWeather  = repository.getCurrentWeather(lat, lon, apiUnits, apiLang).first() != null
                val hasCachedForecast = repository.getForecast(lat, lon, apiUnits, apiLang).first().isNotEmpty()

                if (!hasCachedWeather || !hasCachedForecast) {
                    _uiState.value = HomeUiState.Offline
                }
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps    = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    class Factory(
        private val repository: WeatherRepositoryImp,
        private val locationTracker: LocationUtil,
        private val settingsRepository: SettingsRepositoryImp
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository, locationTracker, settingsRepository) as T
    }
}