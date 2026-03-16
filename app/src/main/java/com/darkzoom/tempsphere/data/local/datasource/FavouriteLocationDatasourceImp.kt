package com.darkzoom.tempsphere.data.local.datasource

import com.darkzoom.tempsphere.data.contract.FavouriteLocationDatasource
import com.darkzoom.tempsphere.data.local.dao.FavouriteLocationDao
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.Flow

class FavouriteLocationDatasourceImp(
    private val dao: FavouriteLocationDao
) : FavouriteLocationDatasource {

    override fun getAllFavourites(): Flow<List<FavLocationEntity>> =
        dao.getAllFavourites()

    override suspend fun getFavouriteById(id: Int): FavLocationEntity? =
        dao.getFavouriteById(id)

    override suspend fun isFavourite(lat: Double, lon: Double): Boolean =
        dao.isFavourite(lat, lon)

    override suspend fun insertFavourite(entity: FavLocationEntity): Long =
        dao.insertFavourite(entity)

    override suspend fun updateFavourite(entity: FavLocationEntity) =
        dao.updateFavourite(entity)

    override suspend fun deleteFavouriteById(id: Int) =
        dao.deleteFavouriteById(id)
}