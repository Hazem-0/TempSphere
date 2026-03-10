package com.darkzoom.tempsphere.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkzoom.tempsphere.R
import com.darkzoom.tempsphere.ui.settings.components.ExpandableRow
import com.darkzoom.tempsphere.ui.settings.components.GlassSection
import com.darkzoom.tempsphere.ui.settings.components.GlassToggle
import com.darkzoom.tempsphere.ui.settings.components.SettingsRow
import com.darkzoom.tempsphere.ui.theme.TempSphereExtendedTheme
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme

@Composable
fun SettingsScreen() {
    var locationMode by remember { mutableStateOf("GPS") }
    var tempUnit by remember { mutableStateOf("Fahrenheit") }
    var windUnit by remember { mutableStateOf("m/s") }
    var language by remember { mutableStateOf("English") }
    var theme by remember { mutableStateOf("Starry Night") }
    var notifications by remember { mutableStateOf(true) }
    var dataRefresh by remember { mutableStateOf("30 min") }

    var openRow by remember { mutableStateOf<String?>(null) }

    fun toggleRow(row: String) {
        openRow = if (openRow == row) null else row
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    icon = if (locationMode == "GPS") Icons.Rounded.LocationOn else Icons.Rounded.Map,
                    iconColor = TempSphereExtendedTheme.colors.emerald,
                    label = stringResource(R.string.location_method),
                    selected = locationMode,
                    options = listOf("GPS", stringResource(R.string.map_selection)),
                    onSelect = { locationMode = it },
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
                    selected = tempUnit,
                    options = listOf("Celsius", "Fahrenheit", "Kelvin"),
                    onSelect = { tempUnit = it },
                    accentColor = TempSphereExtendedTheme.colors.pink,
                    isOpen = openRow == "temp",
                    onToggle = { toggleRow("temp") }
                )
                ExpandableRow(
                    icon = Icons.Rounded.Air,
                    iconColor = TempSphereExtendedTheme.colors.blue,
                    label = stringResource(R.string.wind_speed),
                    selected = windUnit,
                    options = listOf("m/s", "km/h", "mph"),
                    onSelect = { windUnit = it },
                    accentColor = TempSphereExtendedTheme.colors.blue,
                    isOpen = openRow == "wind",
                    onToggle = { toggleRow("wind") },
                    isLast = true
                )
            }

            GlassSection(title = stringResource(R.string.appearance_language)) {
                ExpandableRow(
                    icon = Icons.Rounded.Language,
                    iconColor = TempSphereExtendedTheme.colors.purple,
                    label = stringResource(R.string.language),
                    selected = language,
                    options = listOf("English", "Arabic"),
                    onSelect = { language = it },
                    accentColor = TempSphereExtendedTheme.colors.purple,
                    isOpen = openRow == "language",
                    onToggle = { toggleRow("language") }
                )
                ExpandableRow(
                    icon = Icons.Rounded.Nightlight,
                    iconColor = TempSphereExtendedTheme.colors.lightPurple,
                    label = stringResource(R.string.theme),
                    selected = theme,
                    options = listOf("Dark", "Light"),
                    onSelect = { theme = it },
                    accentColor = TempSphereExtendedTheme.colors.lightPurple,
                    isOpen = openRow == "theme",
                    onToggle = { toggleRow("theme") },
                    isLast = true
                )
            }

            GlassSection(title = stringResource(R.string.data_notifications)) {
                SettingsRow(
                    icon = Icons.Rounded.Notifications,
                    iconColor = TempSphereExtendedTheme.colors.orange,
                    label = stringResource(R.string.push_notifications),
                    value = if (notifications) stringResource(R.string.enabled)
                    else stringResource(R.string.disabled),
                    rightEl = {
                        GlassToggle(
                            enabled = notifications,
                            onToggle = { notifications = !notifications },
                            color = TempSphereExtendedTheme.colors.orange
                        )
                    }
                )
                ExpandableRow(
                    icon = Icons.Rounded.Security,
                    iconColor = TempSphereExtendedTheme.colors.emerald,
                    label = stringResource(R.string.data_refresh_rate),
                    selected = dataRefresh,
                    options = listOf("10 min", "15 min", "30 min", "1 hour"),
                    onSelect = { dataRefresh = it },
                    accentColor = TempSphereExtendedTheme.colors.emerald,
                    isOpen = openRow == "refresh",
                    onToggle = { toggleRow("refresh") },
                    isLast = true
                )
            }
        }
    }
}

@Preview(name = "Dark Mode", showSystemUi = true)
@Composable
fun SettingsScreenDarkPreview() {
    TempSphereTheme(darkTheme = true) {
        SettingsScreen()
    }
}

@Preview(name = "Light Mode", showSystemUi = true)
@Composable
fun SettingsScreenLightPreview() {
    TempSphereTheme(darkTheme = false) {
        SettingsScreen()
    }
}