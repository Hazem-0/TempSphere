package com.darkzoom.tempsphere.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.darkzoom.tempsphere.data.local.datasource.SharedPrefDatasourceImp // Added this import
import com.darkzoom.tempsphere.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(
        val locationMode: String,
        val tempUnit: String,
        val windUnit: String,
        val language: String,
        val dataRefresh: String
    ) : SettingsUiState()
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(
        SettingsUiState.Success(
            locationMode = repository.locationMode,
            tempUnit = repository.tempUnit,
            windUnit = repository.windUnit,
            language = repository.language,
            dataRefresh = repository.dataRefreshRate
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private fun updateState(update: (SettingsUiState.Success) -> SettingsUiState.Success) {
        _uiState.update { currentState ->
            if (currentState is SettingsUiState.Success) {
                update(currentState)
            } else {
                currentState
            }
        }
    }

    fun updateLocationMode(mode: String) {
        repository.locationMode = mode
        updateState { it.copy(locationMode = mode) }
    }

    fun updateTempUnit(unit: String) {
        repository.tempUnit = unit
        updateState { it.copy(tempUnit = unit) }
    }

    fun updateWindUnit(unit: String) {
        repository.windUnit = unit
        updateState { it.copy(windUnit = unit) }
    }

    fun updateLanguage(lang: String) {
        repository.language = lang
        updateState { it.copy(language = lang) }
    }



    fun updateDataRefresh(rate: String) {
        repository.dataRefreshRate = rate
        updateState { it.copy(dataRefresh = rate) }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val prefs = context.getSharedPreferences("tempsphere_preferences", Context.MODE_PRIVATE)
            val datasource = SharedPrefDatasourceImp(prefs)
            val repository = SettingsRepository.getInstance(datasource)
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}