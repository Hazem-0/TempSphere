package com.darkzoom.tempsphere.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.ui.settings.components.ExpandableRow
import com.darkzoom.tempsphere.ui.settings.components.GlassSection
import com.darkzoom.tempsphere.ui.settings.components.GlassToggle
import com.darkzoom.tempsphere.ui.settings.components.SettingsRow
import com.darkzoom.tempsphere.ui.theme.TempSphereExtendedTheme

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        is SettingsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFA78BFA))
            }
        }
        is SettingsUiState.Success -> {
            SettingsContent(state = currentState, viewModel = viewModel)
        }
    }
}

@Composable
private fun SettingsContent(state: SettingsUiState.Success, viewModel: SettingsViewModel) {
    var openRow by remember { mutableStateOf<String?>(null) }

    fun toggleRow(row: String) {
        openRow = if (openRow == row) null else row
    }

    Column(
        modifier = Modifier
            .fillMaxSize()

            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = stringResource(R.string.customize_your_experience),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Spacer(modifier = Modifier.height(20.dp))

            GlassSection(title = stringResource(R.string.location)) {
                ExpandableRow(
                    icon = if (state.locationMode == "GPS") Icons.Rounded.LocationOn else Icons.Rounded.Map,
                    iconColor = TempSphereExtendedTheme.colors.emerald,
                    label = stringResource(R.string.location_method),
                    selected = state.locationMode,
                    options = listOf("GPS", stringResource(R.string.map_selection)),
                    onSelect = { viewModel.updateLocationMode(it) },
                    accentColor = TempSphereExtendedTheme.colors.emerald,
                    isOpen = openRow == "location",
                    onToggle = { toggleRow("location") },
                    isLast = true
                )
            }

            GlassSection(title = stringResource(R.string.measurement_units)) {
                ExpandableRow(
                    icon = Icons.Rounded.Thermostat,
                    iconColor = TempSphereExtendedTheme.colors.pink,
                    label = stringResource(R.string.temperature),
                    selected = state.tempUnit,
                    options = listOf("Celsius", "Fahrenheit", "Kelvin"),
                    onSelect = { viewModel.updateTempUnit(it) },
                    accentColor = TempSphereExtendedTheme.colors.pink,
                    isOpen = openRow == "temp",
                    onToggle = { toggleRow("temp") }
                )
                ExpandableRow(
                    icon = Icons.Rounded.Air,
                    iconColor = TempSphereExtendedTheme.colors.blue,
                    label = stringResource(R.string.wind_speed),
                    selected = state.windUnit,
                    options = listOf("m/s", "km/h", "mph"),
                    onSelect = { viewModel.updateWindUnit(it) },
                    accentColor = TempSphereExtendedTheme.colors.blue,
                    isOpen = openRow == "wind",
                    onToggle = { toggleRow("wind") },
                    isLast = true
                )
            }

            GlassSection(title = stringResource(R.string.language)) {
                ExpandableRow(
                    icon = Icons.Rounded.Language,
                    iconColor = TempSphereExtendedTheme.colors.purple,
                    label = stringResource(R.string.language),
                    selected = state.language,
                    options = listOf("English", "Arabic"),
                    onSelect = { viewModel.updateLanguage(it) },
                    accentColor = TempSphereExtendedTheme.colors.purple,
                    isOpen = openRow == "language",
                    onToggle = { toggleRow("language") }
                )

            }

            GlassSection(title = stringResource(R.string.data_refresh_rate)) {

                ExpandableRow(
                    icon = Icons.Rounded.Security,
                    iconColor = TempSphereExtendedTheme.colors.emerald,
                    label = stringResource(R.string.data_refresh_rate),
                    selected = state.dataRefresh,
                    options = listOf("10 min", "15 min", "30 min", "1 hour"),
                    onSelect = { viewModel.updateDataRefresh(it) },
                    accentColor = TempSphereExtendedTheme.colors.emerald,
                    isOpen = openRow == "refresh",
                    onToggle = { toggleRow("refresh") },
                    isLast = true
                )
            }
        }
    }
}