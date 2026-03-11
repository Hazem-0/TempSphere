package com.darkzoom.tempsphere.ui.core

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkzoom.tempsphere.ui.core.Theme.AfternoonColors
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.core.Theme.MorningColors
import com.darkzoom.tempsphere.ui.core.Theme.NightColors
import com.darkzoom.tempsphere.ui.common.components.AfterNoonBackground
import com.darkzoom.tempsphere.ui.common.components.MorningBackground
import com.darkzoom.tempsphere.ui.common.components.NightBackground
import com.darkzoom.tempsphere.ui.core.components.Screen
import com.darkzoom.tempsphere.ui.home.HomeScreen
import com.darkzoom.tempsphere.ui.home.HomeViewModel
import com.darkzoom.tempsphere.ui.navigation.BottomNavBar
import com.darkzoom.tempsphere.ui.settings.SettingsScreen
import com.darkzoom.tempsphere.ui.settings.SettingsViewModel
import com.darkzoom.tempsphere.ui.settings.SettingsViewModelFactory
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val appContainer = application as App
        val repository = appContainer.repository
        val locationTracker = appContainer.locationTracker
        val settingsRepository = appContainer.settingsRepository

        setContent {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val context = LocalContext.current
            val navController = rememberNavController()

            val appTheme = when (currentHour) {
                in 6..11 -> MorningColors
                in 12..17 -> AfternoonColors
                else -> NightColors
            }

            TempSphereTheme(darkTheme = true) {
                CompositionLocalProvider(LocalAppTheme provides appTheme) {

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentHour) {
                            in 6..11 -> MorningBackground()
                            in 12..17 -> AfterNoonBackground()
                            else -> NightBackground()
                        }

                        Scaffold(
                            bottomBar = { BottomNavBar(navController = navController) },
                            containerColor = androidx.compose.ui.graphics.Color.Transparent,
                            // FIX: Override default insets so Scaffold doesn't push the top down
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { paddingValues ->

                            NavHost(
                                navController = navController,
                                startDestination = Screen.Home.route,
                                // paddingValues now ONLY contains the height of the BottomNavBar
                                modifier = Modifier.padding(paddingValues)
                            ) {
                                composable(Screen.Home.route) {
                                    val homeViewModel: HomeViewModel = viewModel(
                                        factory = HomeViewModel.Factory(
                                            repository = repository,
                                            locationTracker = locationTracker,
                                            settingsRepository = settingsRepository
                                        )
                                    )
                                    HomeScreen(viewModel = homeViewModel)
                                }

                                composable(Screen.Settings.route) {
                                    val settingsViewModel: SettingsViewModel = viewModel(
                                        factory = SettingsViewModelFactory(context)
                                    )
                                    SettingsScreen(viewModel = settingsViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}