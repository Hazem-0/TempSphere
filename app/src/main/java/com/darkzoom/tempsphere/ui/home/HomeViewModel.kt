package com.darkzoom.tempsphere.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.contract.SettingsRepository
import com.darkzoom.tempsphere.data.contract.WeatherRepository
import com.darkzoom.tempsphere.data.local.model.DailyWeather
import com.darkzoom.tempsphere.data.local.model.HourlyWeather
import com.darkzoom.tempsphere.data.local.model.WeatherType
import com.darkzoom.tempsphere.data.repository.WeatherRepositoryImp
import com.darkzoom.tempsphere.utils.LocationUtil
import com.darkzoom.tempsphere.utils.toApiLang
import com.darkzoom.tempsphere.utils.toApiUnits
import com.darkzoom.tempsphere.utils.toSuccess
import com.darkzoom.tempsphere.utils.toUnitSymbol
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
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
        val isRefreshing  : Boolean = false,
        val unitSymbol    : String  = "°F",
        val windUnit      : String  = "m/s"
    ) : HomeUiState()
}


class HomeViewModel(
    private val repository        : WeatherRepository,
    private val locationTracker   : LocationUtil,   // owns all location/permission logic
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private var appContext : Context? = null
    private var cacheJob   : Job?     = null


    init {
        viewModelScope.launch {
            combine(
                settingsRepository.tempUnitFlow,
                settingsRepository.languageFlow
            ) { unit, lang -> unit to lang }
                .distinctUntilChanged()
                .drop(1)
                .collect { _ ->
                    appContext?.let { observeWeather(it) }
                }
        }
    }


    private val apiUnits  : String get() = settingsRepository.tempUnit.toApiUnits()
    private val apiLang   : String get() = settingsRepository.language.toApiLang()
    private val unitSymbol: String get() = settingsRepository.tempUnit.toUnitSymbol()


    fun observeWeather(context: Context) {
        appContext = context.applicationContext
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val location = resolveLocation()
            if (location == null) {
                _uiState.value = HomeUiState.Error(
                    context.getString(R.string.location_permission_required_or_gps_disabled)
                )
                return@launch
            }

            val (lat, lon) = location
            val units  = apiUnits
            val lang   = apiLang
            val symbol = unitSymbol
            val wind   = settingsRepository.windUnit

            cacheJob?.cancel()
            cacheJob = launch {
                combine(
                    repository.getCurrentWeather(lat, lon, units, lang),
                    repository.getForecast(lat, lon, units, lang)
                ) { current, forecast ->
                    if (current != null && forecast.isNotEmpty())
                        current.toSuccess(forecast).copy(unitSymbol = symbol, windUnit = wind)
                    else null
                }.collect { state ->
                    if (state != null) {
                        val refreshing =
                            (_uiState.value as? HomeUiState.Success)?.isRefreshing ?: false
                        _uiState.value = state.copy(isRefreshing = refreshing)
                    }
                }
            }

            if (isOnline(context)) {
                val weatherDeferred  = async { repository.refreshCurrentWeather(lat, lon, units, lang) }
                val forecastDeferred = async { repository.refreshForecast(lat, lon, units, lang) }
                val weatherResult = weatherDeferred.await()
                forecastDeferred.await()

                if (weatherResult.isFailure && _uiState.value !is HomeUiState.Success) {
                    _uiState.value = HomeUiState.Error(
                        weatherResult.exceptionOrNull()?.message
                            ?: context.getString(R.string.failed_to_fetch_weather)
                    )
                }
            } else {
                val hasCachedWeather  =
                    repository.getCurrentWeather(lat, lon, units, lang).first() != null
                val hasCachedForecast =
                    repository.getForecast(lat, lon, units, lang).first().isNotEmpty()
                if (!hasCachedWeather || !hasCachedForecast) {
                    _uiState.value = HomeUiState.Offline
                }
            }
        }
    }


    fun refresh(context: Context) {
        appContext = context.applicationContext
        viewModelScope.launch {
            val location = resolveLocation() ?: return@launch
            val (lat, lon) = location

            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                _uiState.value = currentState.copy(isRefreshing = true)
            }

            if (isOnline(context)) {
                val weatherDeferred  = async { repository.refreshCurrentWeather(lat, lon, apiUnits, apiLang) }
                val forecastDeferred = async { repository.refreshForecast(lat, lon, apiUnits, apiLang) }
                weatherDeferred.await()
                forecastDeferred.await()

                val post = _uiState.value
                if (post is HomeUiState.Success) {
                    _uiState.value = post.copy(isRefreshing = false)
                }
            } else {
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(isRefreshing = false)
                }
                val hasCachedWeather  =
                    repository.getCurrentWeather(lat, lon, apiUnits, apiLang).first() != null
                val hasCachedForecast =
                    repository.getForecast(lat, lon, apiUnits, apiLang).first().isNotEmpty()
                if (!hasCachedWeather || !hasCachedForecast) {
                    _uiState.value = HomeUiState.Offline
                }
            }
        }
    }


    private suspend fun resolveLocation(): Pair<Double, Double>? {
        return when (settingsRepository.locationMode) {
            "Map Selection" -> {
                settingsRepository.getMapLocation()
                    ?: locationTracker.getCurrentLocation()
            }
            else -> {
                locationTracker.getCurrentLocation()
            }
        }
    }

    private fun isOnline(context: Context): Boolean {
        val cm      = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps    = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    class Factory(
        private val repository        : WeatherRepositoryImp,
        private val locationTracker   : LocationUtil,
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            HomeViewModel(repository, locationTracker, settingsRepository) as T
    }
}


