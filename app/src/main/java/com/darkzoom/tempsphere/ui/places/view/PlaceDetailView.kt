package com.darkzoom.tempsphere.ui.places.view

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkzoom.tempsphere.data.local.model.ForecastTab
import com.darkzoom.tempsphere.data.local.model.PlaceDetailData
import com.darkzoom.tempsphere.ui.core.Theme.AppThemeColors
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.home.components.GlassSection
import com.darkzoom.tempsphere.ui.home.components.MainSection
import com.darkzoom.tempsphere.ui.places.viewmodel.PlaceDetailUiState
import com.darkzoom.tempsphere.ui.places.viewmodel.PlaceDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailView(
    viewModel: PlaceDetailViewModel,
    onBack: () -> Unit
) {
    val theme = LocalAppTheme.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val screenType = when (uiState) {
        is PlaceDetailUiState.Loading -> "LOADING"
        is PlaceDetailUiState.Error -> "ERROR"
        is PlaceDetailUiState.Success -> "SUCCESS"
    }

    Crossfade(targetState = screenType, label = "detail_state") { type ->
        when (type) {
            "LOADING" -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.accentPrimary)
                }
            }
            "ERROR" -> {
                val msg = (uiState as? PlaceDetailUiState.Error)?.message ?: "Unknown error"
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text(msg, color = theme.textSecondary, fontSize = 14.sp)
                        TextButton(onClick = { viewModel.refresh() }) {
                            Text("Retry", color = theme.accentPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = onBack) {
                            Text("Go back", color = theme.textSecondary.copy(alpha = 0.6f))
                        }
                    }
                }
            }
            "SUCCESS" -> {
                val data = (uiState as? PlaceDetailUiState.Success)?.data
                if (data != null) {
                    SuccessDetailContent(
                        data = data,
                        theme = theme,
                        onBack = onBack,
                        onRefresh = { viewModel.refresh() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessDetailContent(
    data: PlaceDetailData,
    theme: AppThemeColors,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    var activeTab by remember { mutableStateOf(ForecastTab.HOURLY) }

    PullToRefreshBox(
        isRefreshing = data.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = theme.textPrimary.copy(alpha = 0.8f)
                    )
                }

                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Text(
                        text = data.city,
                        color = theme.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = data.country,
                        color = theme.textSecondary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            MainSection(
                tempF = data.temp,
                feelsLikeF = data.feelsLike,
                highF = data.high,
                lowF = data.low,
                description = data.description,
                weatherType = data.weatherType,
                dateLabel = data.dateLabel
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassSection(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                hourlyItems = data.hourly,
                dailyItems = data.daily,
                humidity = data.humidity,
                windMs = data.windMs.toFloat(),
                pressureHpa = data.pressureHpa,
                cloudinessPct = data.cloudinessPct,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}