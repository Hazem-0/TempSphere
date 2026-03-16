package com.darkzoom.tempsphere.ui.places.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.data.local.model.SavedLocation
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import com.darkzoom.tempsphere.data.repository.WeatherRepository
import com.darkzoom.tempsphere.utils.toCachedSavedLocation
import com.darkzoom.tempsphere.utils.updateWith
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class PlacesUiState {
    object Loading : PlacesUiState()
    object Empty : PlacesUiState()
    data class Success(
        val savedLocations: List<SavedLocation>,
        val searchQuery: String = "",
        val suggestions: List<String> = emptyList(),
        val isRefreshing: Boolean = false
    ) : PlacesUiState()
    data class Error(val message: String) : PlacesUiState()
}

class PlacesViewModel(
    private val repository: WeatherRepository,
    private val units: String = "metric",
    private val lang: String = "en"
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlacesUiState>(PlacesUiState.Loading)
    val uiState: StateFlow<PlacesUiState> = _uiState.asStateFlow()

    private var latestEntities: List<FavLocationEntity> = emptyList()

    init {
        observeFavourites()
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            repository.getAllFavourites()
                .catch { e ->
                    _uiState.value = PlacesUiState.Error(e.message ?: "Failed to load places")
                }
                .collectLatest { entities ->
                    latestEntities = entities
                    if (entities.isEmpty()) {
                        _uiState.value = PlacesUiState.Empty
                    } else {
                        val cachedLocations = entities.map { it.toCachedSavedLocation() }

                        val currentSuccess = _uiState.value as? PlacesUiState.Success
                        _uiState.value = PlacesUiState.Success(
                            savedLocations = cachedLocations,
                            searchQuery = currentSuccess?.searchQuery ?: "",
                            suggestions = currentSuccess?.suggestions ?: emptyList(),
                            isRefreshing = currentSuccess?.isRefreshing ?: false
                        )

                        fetchWeatherForAll(entities)
                    }
                }
        }
    }

    private fun fetchWeatherForAll(entities: List<FavLocationEntity>) {
        viewModelScope.launch {
            entities.map { entity ->
                async {
                    val result = repository.getCurrentWeatherForLocation(
                        entity.latitude, entity.longitude, units, lang
                    )
                    if (result.isSuccess) {
                        val response = result.getOrThrow()
                        val updatedEntity = entity.updateWith(response)
                        repository.updateFavourite(updatedEntity)
                    }
                }
            }.awaitAll()

            val currentSuccess = _uiState.value as? PlacesUiState.Success
            if (currentSuccess != null) {
                _uiState.value = currentSuccess.copy(isRefreshing = false)
            }
        }
    }

    fun refreshAll() {
        val current = _uiState.value
        if (current is PlacesUiState.Success) {
            _uiState.value = current.copy(isRefreshing = true)
            fetchWeatherForAll(latestEntities)
        }
    }

    fun removeFavourite(id: Int) {
        viewModelScope.launch {
            repository.removeFavourite(id)
        }
    }

    fun onSearchQueryChange(query: String) {
        val current = _uiState.value as? PlacesUiState.Success ?: return
        val suggestions = if (query.isNotBlank()) {
            current.savedLocations
                .filter { it.city.contains(query, ignoreCase = true) || it.country.contains(query, ignoreCase = true) }
                .map { "${it.city}, ${it.country}" }
        } else emptyList()
        _uiState.value = current.copy(searchQuery = query, suggestions = suggestions)
    }

    fun clearSearch() {
        val current = _uiState.value as? PlacesUiState.Success ?: return
        _uiState.value = current.copy(searchQuery = "", suggestions = emptyList())
    }

    class Factory(
        private val repository: WeatherRepository,
        private val units: String,
        private val lang: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlacesViewModel(repository, units, lang) as T
    }
}