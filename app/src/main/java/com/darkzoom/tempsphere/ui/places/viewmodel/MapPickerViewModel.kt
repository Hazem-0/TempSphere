package com.darkzoom.tempsphere.ui.places.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class MapPickerUiState {
    object Idle : MapPickerUiState()
    object Resolving : MapPickerUiState()

    data class Resolved(
        val city: String,
        val country: String,
        val latitude: Double,
        val longitude: Double
    ) : MapPickerUiState()

    object Saving : MapPickerUiState()
    object SavedSuccess : MapPickerUiState()
    data class Error(val message: String) : MapPickerUiState()
}


class MapPickerViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MapPickerUiState>(MapPickerUiState.Idle)
    val uiState: StateFlow<MapPickerUiState> = _uiState.asStateFlow()


    fun onLocationPicked(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = MapPickerUiState.Resolving
            val result = repository.resolveLocationName(lat, lon)
            if (result.isSuccess) {
                val (city, country) = result.getOrThrow()
                _uiState.value = MapPickerUiState.Resolved(
                    city = city,
                    country = country,
                    latitude = lat,
                    longitude = lon
                )
            } else {
                _uiState.value = MapPickerUiState.Error(
                    result.exceptionOrNull()?.message ?: "Could not resolve location"
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
            if (result.isSuccess) {
                _uiState.value = MapPickerUiState.SavedSuccess
            } else {
                _uiState.value = MapPickerUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to save location"
                )
            }
        }
    }

    fun resetError() {
        _uiState.value = MapPickerUiState.Idle
    }


    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MapPickerViewModel(repository) as T
    }
}