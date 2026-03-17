package com.darkzoom.tempsphere.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.data.local.datasource.SharedPrefDatasourceImp
import com.darkzoom.tempsphere.data.repository.SettingsRepositoryImp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(
        val locationMode : String,
        val tempUnit     : String,
        val windUnit     : String,
        val language     : String,
        val dataRefresh  : String
    ) : SettingsUiState()
}


sealed class SettingsEvent {
    object OpenMapPicker : SettingsEvent()

    object RestartActivity : SettingsEvent()
}


class SettingsViewModel(private val repository: SettingsRepositoryImp) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(
        SettingsUiState.Success(
            locationMode = repository.locationMode,
            tempUnit     = repository.tempUnit,
            windUnit     = repository.windUnit,
            language     = repository.language,
            dataRefresh  = repository.dataRefreshRate
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()


    fun updateLocationMode(mode: String) {
        repository.locationMode = mode
        updateSuccess { it.copy(locationMode = mode) }
        if (mode == "Map Selection") {
            viewModelScope.launch { _events.emit(SettingsEvent.OpenMapPicker) }
        }
    }

    fun updateTempUnit(unit: String) {
        repository.tempUnit = unit
        updateSuccess { it.copy(tempUnit = unit) }
    }

    fun updateWindUnit(unit: String) {
        repository.windUnit = unit
        updateSuccess { it.copy(windUnit = unit) }
    }


    fun updateLanguage(lang: String) {
        repository.language = lang
        updateSuccess { it.copy(language = lang) }
        viewModelScope.launch { _events.emit(SettingsEvent.RestartActivity) }
    }

    fun updateDataRefresh(rate: String) {
        repository.dataRefreshRate = rate
        updateSuccess { it.copy(dataRefresh = rate) }
    }


    private fun updateSuccess(transform: (SettingsUiState.Success) -> SettingsUiState.Success) {
        _uiState.update { current ->
            if (current is SettingsUiState.Success) transform(current) else current
        }
    }
}


class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            val prefs      = context.getSharedPreferences("tempsphere_preferences", Context.MODE_PRIVATE)
            val datasource = SharedPrefDatasourceImp(prefs)
            val repository = SettingsRepositoryImp.getInstance(datasource)
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}