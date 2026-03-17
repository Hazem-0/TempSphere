package com.darkzoom.tempsphere.ui.places.viewmodel

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.local.model.SavedLocation
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import com.darkzoom.tempsphere.data.repository.WeatherRepositoryImp
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
    object Empty   : PlacesUiState()
    data class Success(
        val savedLocations : List<SavedLocation>,
        val searchQuery    : String       = "",
        val suggestions    : List<String> = emptyList(),
        val isRefreshing   : Boolean      = false
    ) : PlacesUiState()
    data class Error(val message: String) : PlacesUiState()
}

class PlacesViewModel(
    private val repository : WeatherRepositoryImp,
    private val units      : String = "metric",
    private val lang       : String = "en",
    private val context    : Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlacesUiState>(PlacesUiState.Loading)
    val uiState: StateFlow<PlacesUiState> = _uiState.asStateFlow()

    private var latestEntities: List<FavLocationEntity> = emptyList()

    private val fetchedIds = mutableSetOf<Int>()

    init {
        observeFavourites(context)
    }


    private fun observeFavourites(context : Context) {
        viewModelScope.launch {
            repository.getAllFavourites()
                .catch { e ->
                    _uiState.value = PlacesUiState.Error(e.message ?: context.getString(R.string.failed_to_load_places))
                }
                .collectLatest { entities ->
                    latestEntities = entities

                    if (entities.isEmpty()) {
                        _uiState.value = PlacesUiState.Empty
                        return@collectLatest
                    }
                    val cachedLocations = entities.map { it.toCachedSavedLocation() }
                    val currentSuccess  = _uiState.value as? PlacesUiState.Success
                    _uiState.value = PlacesUiState.Success(
                        savedLocations = cachedLocations,
                        searchQuery    = currentSuccess?.searchQuery    ?: "",
                        suggestions    = currentSuccess?.suggestions    ?: emptyList(),
                        isRefreshing   = currentSuccess?.isRefreshing   ?: false
                    )

                    val newEntities = entities.filter { it.id !in fetchedIds }
                    if (newEntities.isNotEmpty()) {
                        fetchWeatherForEntities(newEntities)
                    }
                }
        }
    }


    private fun fetchWeatherForEntities(entities: List<FavLocationEntity>) {
        viewModelScope.launch {

            fetchedIds.addAll(entities.map { it.id })

            entities.map { entity ->
                async {
                    val result = repository.getCurrentWeatherForLocation(
                        entity.latitude, entity.longitude, units, lang
                    )
                    if (result.isSuccess) {
                        val updatedEntity = entity.updateWith(result.getOrThrow())
                        repository.updateFavourite(updatedEntity)
                    }
                }
            }.awaitAll()

            val current = _uiState.value as? PlacesUiState.Success
            if (current != null) {
                _uiState.value = current.copy(isRefreshing = false)
            }
        }
    }


    fun refreshAll() {
        val current = _uiState.value as? PlacesUiState.Success ?: return
        _uiState.value = current.copy(isRefreshing = true)
        fetchedIds.clear()
        fetchWeatherForEntities(latestEntities)
    }


    fun removeFavourite(id: Int) {
        viewModelScope.launch {
            fetchedIds.remove(id)
            repository.removeFavourite(id)
        }
    }

    fun onSearchQueryChange(query: String) {
        val current = _uiState.value as? PlacesUiState.Success ?: return
        val suggestions = if (query.isNotBlank()) {
            current.savedLocations.filter {
                it.city.contains(query, ignoreCase = true) ||
                        it.country.contains(query, ignoreCase = true)
            }.map { "${it.city}, ${it.country}" }
        } else emptyList()
        _uiState.value = current.copy(searchQuery = query, suggestions = suggestions)
    }

    fun clearSearch() {
        val current = _uiState.value as? PlacesUiState.Success ?: return
        _uiState.value = current.copy(searchQuery = "", suggestions = emptyList())
    }


    class Factory(
        private val repository : WeatherRepositoryImp,
        private val units      : String,
        private val lang       : String,
        private val context    : Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PlacesViewModel(repository, units, lang ,   context ) as T
    }
}