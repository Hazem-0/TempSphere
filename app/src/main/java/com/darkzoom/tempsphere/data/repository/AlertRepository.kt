package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.local.datasource.AlertLocalDatasource
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.utils.AlertManager
import kotlinx.coroutines.flow.Flow


class AlertRepository(
    private val localDatasource: AlertLocalDatasource,
    private val alertManager: AlertManager
) {


    fun getAllAlerts(): Flow<List<AlertModel>> = localDatasource.getAllAlerts()

    suspend fun getAlertById(id: Int): AlertModel? = localDatasource.getAlertById(id)

    suspend fun getEnabledAlerts(): List<AlertModel> = localDatasource.getEnabledAlerts()


    suspend fun addAlert(alert: AlertModel) {
        localDatasource.add(alert)
        if (alert.isEnabled) alertManager.scheduleAlert(alert)
    }

    suspend fun toggleAlert(alert: AlertModel) {
        val newEnabled = !alert.isEnabled
        localDatasource.setEnabled(alert.id, newEnabled)
        if (newEnabled) alertManager.scheduleAlert(alert.copy(isEnabled = true))
        else            alertManager.cancelAlert(alert)
    }

    suspend fun deleteAlert(alert: AlertModel) {
        alertManager.cancelAlert(alert)
        localDatasource.delete(alert.id)
    }


    suspend fun handleAlertFired(alertId: Int) {
        val alert = localDatasource.getAlertById(alertId) ?: return

        if (alert.repeatMode == RepeatMode.ONCE) {
            localDatasource.setEnabled(alertId, false)
        } else {
            alertManager.scheduleNextOccurrence(alert)
        }
    }


    suspend fun rescheduleAllEnabled() {
        localDatasource.getEnabledAlerts().forEach { alertManager.scheduleAlert(it) }
    }
}