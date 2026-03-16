package com.darkzoom.tempsphere.alert

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.darkzoom.tempsphere.data.contract.AlertRepository
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.ui.alert.AlertViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
class AlertViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var alertRepository: AlertRepository
    private lateinit var viewModel: AlertViewModel

    private val alertsFlow = MutableStateFlow<List<AlertModel>>(emptyList())

    private val sampleNotification = AlertModel(
        id         = 1,
        timeText   = "08:00 AM",
        alertType  = "Notification",
        isEnabled  = true,
        hour       = 8,
        minute     = 0,
        repeatMode = RepeatMode.ONCE
    )


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        alertRepository = mockk(relaxed = true)

        every { alertRepository.getAllAlerts() } returns alertsFlow
        viewModel = AlertViewModel(alertRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun alerts_repositoryEmitsList_viewModelExposesIt() = runTest {
        alertsFlow.value = listOf(sampleNotification)
        val emitted = viewModel.alerts.first { it.isNotEmpty() }
        assertEquals(1, emitted.size)
        assertEquals("08:00 AM", emitted[0].timeText)
    }

    @Test
    fun addAlert_callsRepositoryAddAlert_withCorrectData() = runTest {
        viewModel.addAlert("08:00 AM", "Alarm", 8, 0, repeatMode = RepeatMode.ONCE)


        coVerify {
            alertRepository.addAlert(match {
                it.timeText == "08:00 AM" &&
                        it.alertType == "Alarm" &&
                        it.hour == 8 &&
                        it.minute == 0 &&
                        it.repeatMode == RepeatMode.ONCE
            })
        }
    }

    @Test
    fun toggleAlert_callsRepositoryToggleAlert() = runTest {
        viewModel.toggleAlert(sampleNotification)
        coVerify { alertRepository.toggleAlert(sampleNotification) }
    }

    @Test
    fun deleteAlert_callsRepositoryDeleteAlert() = runTest {
        viewModel.deleteAlert(sampleNotification)
        coVerify { alertRepository.deleteAlert(sampleNotification) }
    }

    @Test
    fun alerts_flowUpdates_viewModelReflectsNewState() = runTest {
        assertTrue(viewModel.alerts.first().isEmpty())
        alertsFlow.value = listOf(sampleNotification)
        val updated = viewModel.alerts.first { it.isNotEmpty() }
        assertEquals(1, updated.size)
        assertEquals(sampleNotification.id, updated[0].id)
    }
}