package com.darkzoom.tempsphere.data.local.datasource

import com.darkzoom.tempsphere.data.contract.AlertLocalDatasource
import com.darkzoom.tempsphere.data.local.dao.AlertDao

import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.utils.toDomain
import com.darkzoom.tempsphere.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AlertLocalDatasourceImp(private val alertDao: AlertDao) : AlertLocalDatasource {

    override fun getAllAlerts(): Flow<List<AlertModel>> =
        alertDao.getAllAlerts().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getAlertById(id: Int): AlertModel? =
        alertDao.getAlertById(id)?.toDomain()

    override suspend fun getEnabledAlerts(): List<AlertModel> =
        alertDao.getEnabledAlerts().map { it.toDomain() }

    override suspend fun add(alert: AlertModel) =
        alertDao.insert(alert.toEntity())

    override suspend fun setEnabled(id: Int, enabled: Boolean) =
        alertDao.setEnabled(id, enabled)

    override suspend fun delete(id: Int) =
        alertDao.deleteById(id)

    override suspend fun deleteAll() =
        alertDao.deleteAll()
}