package com.darkzoom.tempsphere.data.local.Dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.darkzoom.tempsphere.data.local.dao.FavouriteLocationDao
import com.darkzoom.tempsphere.data.local.db.WeatherDatabase
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FavouriteLocationDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: FavouriteLocationDao

    private val cairo = FavLocationEntity(
        id        = 0,
        city      = "Cairo",
        country   = "EG",
        latitude  = 30.0,
        longitude = 31.0
    )



    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
        dao = database.favouriteLocationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertFavourite_getAllFavourites_containsInsertedItem() = runTest {
        dao.insertFavourite(cairo)

        val results = dao.getAllFavourites().first()

        assertEquals(1, results.size)
        assertEquals("Cairo", results[0].city)
    }

    @Test
    fun insertFavourite_isFavourite_returnsTrue() = runTest {
        dao.insertFavourite(cairo)

        val result = dao.isFavourite(cairo.latitude, cairo.longitude)

        assertTrue(result)
    }



    @Test
    fun deleteFavouriteById_thenGetById_returnsNull() = runTest {
        val insertedId = dao.insertFavourite(cairo).toInt()

        dao.deleteFavouriteById(insertedId)
        val result = dao.getFavouriteById(insertedId)

        assertNull(result)
    }




}