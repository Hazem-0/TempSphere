package com.darkzoom.tempsphere.data.local.dao

import androidx.room.*
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteLocationDao {

    @Query("SELECT * FROM favourite_locations ORDER BY addedAt DESC")
    fun getAllFavourites(): Flow<List<FavLocationEntity>>

    @Query("SELECT * FROM favourite_locations WHERE id = :id")
    suspend fun getFavouriteById(id: Int): FavLocationEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favourite_locations WHERE latitude = :lat AND longitude = :lon)")
    suspend fun isFavourite(lat: Double, lon: Double): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(entity: FavLocationEntity): Long

    @Delete
    suspend fun deleteFavourite(entity: FavLocationEntity)

    @Query("DELETE FROM favourite_locations WHERE id = :id")
    suspend fun deleteFavouriteById(id: Int)

    @Query("DELETE FROM favourite_locations")
    suspend fun deleteAllFavourites()
}