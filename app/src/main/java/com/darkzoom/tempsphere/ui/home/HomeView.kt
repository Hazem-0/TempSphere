package com.darkzoom.tempsphere.ui.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.data.local.model.ForecastTab
import com.darkzoom.tempsphere.ui.core.components.GlassSection
import com.darkzoom.tempsphere.ui.core.components.MainSection
import com.darkzoom.tempsphere.ui.home.components.*
import com.darkzoom.tempsphere.ui.theme.AccentPurple

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state   by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->

        viewModel.observeWeather(context)
    }

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    fun requestPermission() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        if (hasPermission()) {
            viewModel.observeWeather(context)
        } else {
            requestPermission()
        }
    }

    val onRetry: () -> Unit = {
        if (hasPermission()) {
            viewModel.observeWeather(context)
        } else {
            requestPermission()
        }
    }

    val screenType = when (state) {
        is HomeUiState.Loading -> "LOADING"
        is HomeUiState.Offline -> "OFFLINE"
        is HomeUiState.Error   -> "ERROR"
        is HomeUiState.Success -> "SUCCESS"
    }

    Crossfade(targetState = screenType, label = "home_state") { type ->
        when (type) {
            "LOADING" -> LoadingContent()
            "OFFLINE" -> OfflineContent(onRetry = onRetry)
            "ERROR"   -> {
                val errorState = state as? HomeUiState.Error
                ErrorContent(
                    message = errorState?.message ?: "Error",
                    onRetry = onRetry
                )
            }
            "SUCCESS" -> {
                val successState = state as? HomeUiState.Success
                if (successState != null) {
                    SuccessContent(
                        state     = successState,
                        onRefresh = { viewModel.refresh(context) }
                    )
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(message, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            TextButton(onClick = onRetry) {
                Text(
                    stringResource(R.string.retry),
                    color      = AccentPurple,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}



@Composable
private fun OfflineContent(onRetry: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(
        label = stringResource(R.string.offline_pulse)
    )
    val iconScale by infiniteTransition.animateFloat(
        initialValue  = 0.92f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = stringResource(R.string.icon_scale)
    )

    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0D0D1A), Color(0xFF12122B)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier            = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(iconScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF3B1F72).copy(alpha = 0.6f), Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "☁️", fontSize = 52.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text       = stringResource(R.string.no_connection),
                color      = Color.White,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = stringResource(R.string.we_couldn_t_fetch_the_weather_no_cached_data_is_available_yet),
                color      = Color.White.copy(alpha = 0.5f),
                fontSize   = 13.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(AccentPurple.copy(alpha = 0.4f))
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick        = onRetry,
                shape          = RoundedCornerShape(50),
                colors         = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor   = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 36.dp, vertical = 14.dp),
                elevation      = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Text(
                    text          = stringResource(R.string.try_again),
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
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
                dateLabel   = state.dateLabel,
                unitSymbol  = state.unitSymbol
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
                unitSymbol    = state.unitSymbol,
                windUnit      = state.windUnit,
                modifier      = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}