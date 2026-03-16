package com.darkzoom.tempsphere.data.local.datasource

import com.darkzoom.tempsphere.data.local.dao.FavouriteLocationDao
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.Flow

class FavouriteLocalDatasource(
    private val dao: FavouriteLocationDao
) {
    fun getAllFavourites(): Flow<List<FavLocationEntity>> =
        dao.getAllFavourites()

    suspend fun getFavouriteById(id: Int): FavLocationEntity? =
        dao.getFavouriteById(id)

    suspend fun isFavourite(lat: Double, lon: Double): Boolean =
        dao.isFavourite(lat, lon)

    suspend fun insertFavourite(entity: FavLocationEntity): Long =
        dao.insertFavourite(entity)

    suspend fun updateFavourite(entity: FavLocationEntity) =
        dao.updateFavourite(entity)

    suspend fun deleteFavouriteById(id: Int) =
        dao.deleteFavouriteById(id)
}