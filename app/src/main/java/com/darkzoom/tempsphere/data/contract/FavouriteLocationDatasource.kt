package com.darkzoom.tempsphere.data.contract

import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.Flow

interface FavouriteLocationDatasource {
    fun getAllFavourites(): Flow<List<FavLocationEntity>>
    suspend fun getFavouriteById(id: Int): FavLocationEntity?
    suspend fun isFavourite(lat: Double, lon: Double): Boolean
    suspend fun insertFavourite(entity: FavLocationEntity): Long
    suspend fun updateFavourite(entity: FavLocationEntity)
    suspend fun deleteFavouriteById(id: Int)
}