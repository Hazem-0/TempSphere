package com.darkzoom.tempsphere.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darkzoom.tempsphere.data.local.model.entity.AlertEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts ORDER BY (hour * 60 + minute) ASC")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE id = :id LIMIT 1")
    suspend fun getAlertById(id: Int): AlertEntity?

    @Query("SELECT * FROM alerts WHERE is_enabled = 1")
    suspend fun getEnabledAlerts(): List<AlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)

    @Query("UPDATE alerts SET is_enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Int, enabled: Boolean)

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM alerts")
    suspend fun deleteAll()
}