package com.darkzoom.tempsphere.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.darkzoom.tempsphere.data.contract.SettingsRepository
import com.darkzoom.tempsphere.data.contract.WeatherRepository
import com.darkzoom.tempsphere.utils.LocationUtil
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: WeatherRepository
    private lateinit var locationUtil: LocationUtil
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel

    private lateinit var context: android.content.Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        repository = mockk(relaxed = true)
        locationUtil = mockk()
        settingsRepository = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { settingsRepository.tempUnit } returns "Celsius"
        every { settingsRepository.language } returns "English"
        every { repository.getCurrentWeather(any(), any(), any(), any()) } returns MutableStateFlow(null)
        every { repository.getForecast(any(), any(), any(), any()) } returns MutableStateFlow(emptyList())
        coEvery { locationUtil.getCurrentLocation() } returns Pair(30.0, 31.0)

        viewModel = HomeViewModel(repository, locationUtil, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeWeather_locationNull_setsErrorState() = runTest {
        coEvery { locationUtil.getCurrentLocation() } returns null

        viewModel.observeWeather(context)

        assertTrue(viewModel.uiState.value is HomeUiState.Error)
    }


    @Test
    fun initialState_isLoading() = runTest {
        assertTrue(viewModel.uiState.value is HomeUiState.Loading)
    }

    @Test
    fun refresh_locationNull_doesNotChangeState() = runTest {
        coEvery { locationUtil.getCurrentLocation() } returns null

        val stateBefore = viewModel.uiState.value
        viewModel.refresh(context)

        assertEquals(stateBefore, viewModel.uiState.value)
    }


}