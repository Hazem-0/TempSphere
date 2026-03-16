package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.contract.FavouriteLocationDatasource
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavouriteLocalDatasource : FavouriteLocationDatasource {

    private val store = mutableListOf<FavLocationEntity>()
    private var nextId = 1
    private val _flow = MutableStateFlow<List<FavLocationEntity>>(emptyList())

    private fun emit() { _flow.value = store.toList() }

    override fun getAllFavourites(): Flow<List<FavLocationEntity>> = _flow

    override suspend fun getFavouriteById(id: Int): FavLocationEntity? =
        store.firstOrNull { it.id == id }

    override suspend fun isFavourite(lat: Double, lon: Double): Boolean =
        store.any { it.latitude == lat && it.longitude == lon }

    override suspend fun insertFavourite(entity: FavLocationEntity): Long {
        val toInsert = entity.copy(id = nextId++)
        store.add(toInsert)
        emit()
        return toInsert.id.toLong()
    }

    override suspend fun updateFavourite(entity: FavLocationEntity) {
        val idx = store.indexOfFirst { it.id == entity.id }
        if (idx >= 0) { store[idx] = entity; emit() }
    }

    override suspend fun deleteFavouriteById(id: Int) {
        store.removeAll { it.id == id }
        emit()
    }
}