package com.darkzoom.tempsphere.ui.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.data.repository.AlertRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class AlertViewModel(
    private val alertRepository: AlertRepository
) : ViewModel() {


    val alerts: StateFlow<List<AlertModel>> = alertRepository
        .getAllAlerts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun addAlert(timeText: String, type: String, hour: Int, minute: Int, repeatMode: RepeatMode) {
        viewModelScope.launch {
            val alert = AlertModel(
                id         = System.currentTimeMillis().toInt(),
                timeText   = timeText,
                alertType  = type,
                isEnabled  = true,
                hour       = hour,
                minute     = minute,
                repeatMode = repeatMode
            )
            alertRepository.addAlert(alert)
        }
    }

    fun toggleAlert(alert: AlertModel) {
        viewModelScope.launch { alertRepository.toggleAlert(alert) }
    }

    fun deleteAlert(alert: AlertModel) {
        viewModelScope.launch { alertRepository.deleteAlert(alert) }
    }


    class Factory(private val alertRepository: AlertRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AlertViewModel(alertRepository) as T
    }
}