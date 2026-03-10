package com.darkzoom.tempsphere.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkzoom.tempsphere.data.remote.model.ForecastTab
import com.darkzoom.tempsphere.data.remote.model.HomeUiState
import com.darkzoom.tempsphere.ui.home.components.*
import com.darkzoom.tempsphere.ui.theme.AccentPurple


@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // The VIEW is dumb. It just opens the Android permission box.
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Tell the ViewModel to run logic now that we might have permission
        viewModel.loadWeather()
    }

    // Ask on first launch
    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    // Ask on pull-to-refresh
    val onUserRefresh = {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    val screenType = when(state) {
        is HomeUiState.Loading -> "LOADING"
        is HomeUiState.Error   -> "ERROR"
        is HomeUiState.Success -> "SUCCESS"
    }

    Crossfade(targetState = screenType, label = "home_state") { type ->
        when (type) {
            "LOADING" -> LoadingContent()
            "ERROR"   -> {
                val errorState = state as? HomeUiState.Error
                ErrorContent(message = errorState?.message ?: "Error", onRetry = onUserRefresh)
            }
            "SUCCESS" -> {
                val successState = state as? HomeUiState.Success
                if (successState != null) {
                    SuccessContent(state = successState, onRefresh = onUserRefresh)
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AccentPurple)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(message, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            TextButton(onClick = onRetry) {
                Text("Retry", color = AccentPurple, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessContent(state: HomeUiState.Success, onRefresh: () -> Unit) {
    var activeTab by remember { mutableStateOf(ForecastTab.HOURLY) }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh    = onRefresh,
        modifier     = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            HomeTopBar(city = state.city)

            MainSection(
                tempF       = state.temp,
                feelsLikeF  = state.feelsLike,
                highF       = state.high,
                lowF        = state.low,
                description = state.description,
                weatherType = state.weatherType,
                dateLabel   = state.dateLabel
            )

            Spacer(Modifier.height(20.dp))

            GlassSection(
                activeTab     = activeTab,
                onTabSelected = { activeTab = it },
                hourlyItems   = state.hourly,
                dailyItems    = state.daily,
                humidity      = state.humidity,
                windMs        = state.windMs,
                pressureHpa   = state.pressureHpa,
                cloudinessPct = state.cloudinessPct,
                modifier      = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}