package com.darkzoom.tempsphere.data.contract

import com.darkzoom.tempsphere.data.local.model.AlertModel
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getAllAlerts(): Flow<List<AlertModel>>

    suspend fun getAlertById(id: Int): AlertModel?

    suspend fun getEnabledAlerts(): List<AlertModel>

    suspend fun addAlert(alert: AlertModel)

    suspend fun toggleAlert(alert: AlertModel)

    suspend fun deleteAlert(alert: AlertModel)

    suspend fun handleAlertFired(alertId: Int)

    suspend fun rescheduleAllEnabled()
}