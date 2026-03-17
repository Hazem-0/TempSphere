package com.darkzoom.tempsphere.ui.places.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.contract.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MapPickerUiState {
    object Idle              : MapPickerUiState()
    object Resolving         : MapPickerUiState()
    data class Resolved(
        val city      : String,
        val country   : String,
        val latitude  : Double,
        val longitude : Double
    ) : MapPickerUiState()
    object Saving            : MapPickerUiState()
    object SavedSuccess      : MapPickerUiState()
    object HomeLocationSaved : MapPickerUiState()
    data class Error(val message: String) : MapPickerUiState()
}

class MapPickerViewModel(
    private val repository: WeatherRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapPickerUiState>(MapPickerUiState.Idle)
    val uiState: StateFlow<MapPickerUiState> = _uiState.asStateFlow()

    var lastResolvedLocation: Pair<Double, Double>? = null
        private set

    fun onLocationPicked(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = MapPickerUiState.Resolving
            val result = repository.resolveLocationName(lat, lon)
            if (result.isSuccess) {
                val (city, country) = result.getOrThrow()
                lastResolvedLocation = Pair(lat, lon)
                _uiState.value = MapPickerUiState.Resolved(
                    city      = city,
                    country   = country,
                    latitude  = lat,
                    longitude = lon
                )
            } else {
                _uiState.value = MapPickerUiState.Error(
                    result.exceptionOrNull()?.message ?: context.getString(R.string.could_not_resolve_location)
                )
            }
        }
    }

    fun saveCurrentLocation() {
        val resolved = _uiState.value as? MapPickerUiState.Resolved ?: return
        viewModelScope.launch {
            _uiState.value = MapPickerUiState.Saving
            val result = repository.addFavourite(
                city    = resolved.city,
                country = resolved.country,
                lat     = resolved.latitude,
                lon     = resolved.longitude
            )
            _uiState.value = if (result.isSuccess) {
                MapPickerUiState.SavedSuccess
            } else {
                MapPickerUiState.Error(
                    result.exceptionOrNull()?.message ?: context.getString(R.string.failed_to_save_location)
                )
            }
        }
    }

    fun saveAsHomeLocation() {
        if (_uiState.value !is MapPickerUiState.Resolved) return
        _uiState.value = MapPickerUiState.HomeLocationSaved
    }

    fun resetError() {
        _uiState.value = MapPickerUiState.Idle
    }

    class Factory(private val repository: WeatherRepository , private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MapPickerViewModel(repository ,context) as T
    }
}