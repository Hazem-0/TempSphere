package com.darkzoom.tempsphere.data.contract

import com.darkzoom.tempsphere.data.local.model.AlertModel
import kotlinx.coroutines.flow.Flow

interface AlertLocalDatasource {
    fun getAllAlerts(): Flow<List<AlertModel>>
    suspend fun getAlertById(id: Int): AlertModel?
    suspend fun getEnabledAlerts(): List<AlertModel>
    suspend fun add(alert: AlertModel)
    suspend fun setEnabled(id: Int, enabled: Boolean)
    suspend fun delete(id: Int)
    suspend fun deleteAll()
}