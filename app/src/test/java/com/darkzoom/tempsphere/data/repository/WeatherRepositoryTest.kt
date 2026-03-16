package com.darkzoom.tempsphere.data.repository

import com.darkzoom.tempsphere.data.contract.WeatherRepository
import com.darkzoom.tempsphere.data.datasource.FakeWeatherRemoteDatasource
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var repository: WeatherRepository
    private lateinit var fakeLocal: FakeWeatherLocalDatasource
    private lateinit var fakeFav: FakeFavouriteLocalDatasource
    private lateinit var fakeRemote: FakeWeatherRemoteDatasource

    @Before
    fun setup() {
        fakeLocal = FakeWeatherLocalDatasource()
        fakeFav = FakeFavouriteLocalDatasource()
        fakeRemote = FakeWeatherRemoteDatasource()

        repository = WeatherRepositoryImp(
            remoteDataSource = fakeRemote,
            localDatasource = fakeLocal,
            favouriteLocalDatasource = fakeFav
        )
    }

    @Test
    fun refreshCurrentWeather_success_cachesEntityInLocal() = runTest {
        val result = repository.refreshCurrentWeather(30.0, 31.0, "metric", "en")

        assertTrue(result.isSuccess)
        val cached = fakeLocal.getCurrentWeather().first()

        assertEquals("Cairo", cached?.cityName)
    }

    @Test
    fun refreshCurrentWeather_networkError_returnsFailure() = runTest {
        fakeRemote = FakeWeatherRemoteDatasource(shouldThrow = true)
        repository = WeatherRepositoryImp(fakeRemote, fakeLocal, fakeFav)

        val result = repository.refreshCurrentWeather(30.0, 31.0, "metric", "en")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun addFavourite_thenGetAllFavourites_containsAddedItem() = runTest {
        repository.addFavourite("Cairo", "EG", 30.0, 31.0)

        val favourites = fakeFav.getAllFavourites().first()

        assertEquals(1, favourites.size)
        assertEquals("Cairo", favourites[0].city)
    }

    @Test
    fun removeFavourite_thenGetAll_doesNotContainRemovedItem() = runTest {
        val insertedId = fakeFav.insertFavourite(
            FavLocationEntity(id = 0, city = "Cairo", country = "EG", latitude = 30.0, longitude = 31.0)
        ).toInt()

        repository.removeFavourite(insertedId)

        val favourites = fakeFav.getAllFavourites().first()
        assertTrue(favourites.none { it.id == insertedId })
    }

    @Test
    fun isFavourite_afterInsert_returnsTrue() = runTest {
        repository.addFavourite("Cairo", "EG", 30.0, 31.0)

        val result = repository.isFavourite(30.0, 31.0)

        assertTrue(result)
    }

    @Test
    fun isFavourite_notInserted_returnsFalse() = runTest {
        val result = repository.isFavourite(99.0, 99.0)

        assertFalse(result)
    }
}