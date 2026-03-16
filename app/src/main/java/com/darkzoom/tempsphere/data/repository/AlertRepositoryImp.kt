package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.contract.AlertLocalDatasource
import com.darkzoom.tempsphere.data.contract.AlertRepository
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.utils.AlertManager
import kotlinx.coroutines.flow.Flow


class AlertRepositoryImp(
    private val localDatasource: AlertLocalDatasource,
    private val alertManager: AlertManager
) : AlertRepository {


    override  fun getAllAlerts(): Flow<List<AlertModel>> = localDatasource.getAllAlerts()

    override  suspend fun getAlertById(id: Int): AlertModel? = localDatasource.getAlertById(id)

    override  suspend fun getEnabledAlerts(): List<AlertModel> = localDatasource.getEnabledAlerts()


    override  suspend fun addAlert(alert: AlertModel) {
        localDatasource.add(alert)
        if (alert.isEnabled) alertManager.scheduleAlert(alert)
    }

    override  suspend fun toggleAlert(alert: AlertModel) {
        val newEnabled = !alert.isEnabled
        localDatasource.setEnabled(alert.id, newEnabled)
        if (newEnabled) alertManager.scheduleAlert(alert.copy(isEnabled = true))
        else            alertManager.cancelAlert(alert)
    }

    override  suspend fun deleteAlert(alert: AlertModel) {
        alertManager.cancelAlert(alert)
        localDatasource.delete(alert.id)
    }


    override  suspend fun handleAlertFired(alertId: Int) {
        val alert = localDatasource.getAlertById(alertId) ?: return

        if (alert.repeatMode == RepeatMode.ONCE) {
            localDatasource.setEnabled(alertId, false)
        } else {
            alertManager.scheduleNextOccurrence(alert)
        }
    }


    override  suspend fun rescheduleAllEnabled() {
        localDatasource.getEnabledAlerts().forEach { alertManager.scheduleAlert(it) }
    }
}