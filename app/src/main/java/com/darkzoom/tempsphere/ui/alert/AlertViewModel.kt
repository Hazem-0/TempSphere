package com.darkzoom.tempsphere.ui.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.utils.AlertManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(
    private val alertManager: AlertManager
) : ViewModel() {

    private val _alerts = MutableStateFlow<List<AlertModel>>(emptyList())
    val alerts: StateFlow<List<AlertModel>> = _alerts.asStateFlow()

    fun addAlert(timeText: String, type: String, hour: Int, minute: Int) {
        viewModelScope.launch {
            val newAlert = AlertModel(
                id = System.currentTimeMillis().toInt(),
                timeText = timeText,
                alertType = type,
                isEnabled = true,
                hour = hour,
                minute = minute
            )
            val currentList = _alerts.value.toMutableList()
            currentList.add(newAlert)
            _alerts.value = currentList
            alertManager.scheduleAlert(newAlert)
        }
    }

    fun toggleAlert(alert: AlertModel) {
        viewModelScope.launch {
            val currentList = _alerts.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == alert.id }
            if (index != -1) {
                val updatedAlert = alert.copy(isEnabled = !alert.isEnabled)
                currentList[index] = updatedAlert
                _alerts.value = currentList

                if (updatedAlert.isEnabled) {
                    alertManager.scheduleAlert(updatedAlert)
                } else {
                    alertManager.cancelAlert(updatedAlert)
                }
            }
        }
    }

    fun deleteAlert(alert: AlertModel) {
        viewModelScope.launch {
            val currentList = _alerts.value.toMutableList()
            currentList.remove(alert)
            _alerts.value = currentList
            alertManager.cancelAlert(alert)
        }
    }

    class Factory(
        private val alertManager: AlertManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlertViewModel(alertManager) as T
        }
    }
}