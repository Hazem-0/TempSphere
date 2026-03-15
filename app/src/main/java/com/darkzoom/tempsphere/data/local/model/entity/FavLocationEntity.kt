package com.darkzoom.tempsphere.data.local.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_locations")
data class FavLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val city: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long = System.currentTimeMillis()
)
