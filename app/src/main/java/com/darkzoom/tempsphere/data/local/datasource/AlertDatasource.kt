package com.darkzoom.tempsphere.data.local.datasource

import com.darkzoom.tempsphere.data.local.dao.AlertDao

import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.utils.toDomain
import com.darkzoom.tempsphere.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AlertLocalDatasource(private val alertDao: AlertDao) {


    fun getAllAlerts(): Flow<List<AlertModel>> =
        alertDao.getAllAlerts().map { entities -> entities.map { it.toDomain() } }


    suspend fun getAlertById(id: Int): AlertModel? =
        alertDao.getAlertById(id)?.toDomain()

    suspend fun getEnabledAlerts(): List<AlertModel> =
        alertDao.getEnabledAlerts().map { it.toDomain() }


    suspend fun add(alert: AlertModel) =
        alertDao.insert(alert.toEntity())

    suspend fun setEnabled(id: Int, enabled: Boolean) =
        alertDao.setEnabled(id, enabled)

    suspend fun delete(id: Int) =
        alertDao.deleteById(id)

    suspend fun deleteAll() =
        alertDao.deleteAll()
}