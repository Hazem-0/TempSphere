package com.darkzoom.tempsphere.ui.places.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.data.local.model.PlaceDetailData
import com.darkzoom.tempsphere.data.repository.WeatherRepository
import com.darkzoom.tempsphere.utils.toPlaceDetailData
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlaceDetailUiState {
    object Loading : PlaceDetailUiState()
    data class Success(val data: PlaceDetailData) : PlaceDetailUiState()
    data class Error(val message: String) : PlaceDetailUiState()
}

class PlaceDetailViewModel(
    private val favouriteId: Int,
    private val repository: WeatherRepository,
    private val units: String = "metric",
    private val lang: String = "en"
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlaceDetailUiState>(PlaceDetailUiState.Loading)
    val uiState: StateFlow<PlaceDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = PlaceDetailUiState.Loading

            val entity = repository.getFavouriteById(favouriteId)
            if (entity == null) {
                _uiState.value = PlaceDetailUiState.Error("Location not found")
                return@launch
            }

            val currentDeferred = async {
                repository.getCurrentWeatherForLocation(entity.latitude, entity.longitude, units, lang)
            }
            val forecastDeferred = async {
                repository.getForecastForLocation(entity.latitude, entity.longitude, units, lang)
            }

            val currentResult = currentDeferred.await()
            val forecastResult = forecastDeferred.await()

            if (currentResult.isFailure || forecastResult.isFailure) {
                _uiState.value = PlaceDetailUiState.Error(
                    currentResult.exceptionOrNull()?.message ?: forecastResult.exceptionOrNull()?.message ?: "Failed to load weather"
                )
                return@launch
            }

            val current = currentResult.getOrThrow()
            val forecast = forecastResult.getOrThrow()

            _uiState.value = PlaceDetailUiState.Success(
                current.toPlaceDetailData(entity.city, entity.country, forecast)
            )
        }
    }

    fun refresh() {
        val current = _uiState.value
        if (current is PlaceDetailUiState.Success) {
            _uiState.value = PlaceDetailUiState.Success(current.data.copy(isRefreshing = true))
        }
        loadDetail()
    }

    class Factory(
        private val favouriteId: Int,
        private val repository: WeatherRepository,
        private val units: String,
        private val lang: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlaceDetailViewModel(favouriteId, repository, units, lang) as T
    }
}