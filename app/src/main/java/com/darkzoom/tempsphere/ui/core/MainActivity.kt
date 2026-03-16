package com.darkzoom.tempsphere.ui.core

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.darkzoom.tempsphere.ui.alert.AlertViewModel
import com.darkzoom.tempsphere.ui.alert.AlertsScreen
import com.darkzoom.tempsphere.ui.common.components.AfterNoonBackground
import com.darkzoom.tempsphere.ui.common.components.MorningBackground
import com.darkzoom.tempsphere.ui.common.components.NightBackground
import com.darkzoom.tempsphere.ui.core.Theme.AfternoonColors
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.core.Theme.MorningColors
import com.darkzoom.tempsphere.ui.core.Theme.NightColors
import com.darkzoom.tempsphere.ui.core.components.Screen
import com.darkzoom.tempsphere.ui.home.HomeScreen
import com.darkzoom.tempsphere.ui.home.HomeViewModel
import com.darkzoom.tempsphere.ui.core.components.BottomNavBar
import com.darkzoom.tempsphere.ui.places.MapPickerActivity
import com.darkzoom.tempsphere.ui.places.view.PlacesView
import com.darkzoom.tempsphere.ui.places.viewmodel.PlacesViewModel
import com.darkzoom.tempsphere.ui.places.view.PlaceDetailView
import com.darkzoom.tempsphere.ui.settings.SettingsScreen
import com.darkzoom.tempsphere.ui.settings.SettingsViewModel
import com.darkzoom.tempsphere.ui.settings.SettingsViewModelFactory
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme
import java.util.Calendar
import com.darkzoom.tempsphere.ui.places.viewmodel.PlaceDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val app               = application as App
        val repository        = app.repository
        val locationTracker   = app.locationTracker
        val settingsRepository = app.settingsRepository
        val alertRepository   = app.alertRepository

        setContent {
            val currentHour   = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val context       = LocalContext.current
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute  = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute != Screen.PlaceDetail.route

            val appTheme = when (currentHour) {
                in 6..11  -> MorningColors
                in 12..17 -> AfternoonColors
                else      -> NightColors
            }

            val mapPickerLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == MapPickerActivity.RESULT_PLACE_SAVED) {
                    navController.navigate(Screen.Places.route) {
                        launchSingleTop = true
                    }
                }
            }

            TempSphereTheme(darkTheme = true) {
                CompositionLocalProvider(LocalAppTheme provides appTheme) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (currentHour) {
                            in 6..11  -> MorningBackground()
                            in 12..17 -> AfterNoonBackground()
                            else      -> NightBackground()
                        }

                        Scaffold(
                            bottomBar = {
                                if (showBottomBar) BottomNavBar(navController = navController)
                            },
                            containerColor   = androidx.compose.ui.graphics.Color.Transparent,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { paddingValues ->

                            NavHost(
                                navController    = navController,
                                startDestination = Screen.Home.route,
                                modifier         = Modifier.padding(paddingValues)
                            ) {
                                composable(Screen.Home.route) {
                                    val homeViewModel: HomeViewModel = viewModel(
                                        factory = HomeViewModel.Factory(
                                            repository         = repository,
                                            locationTracker    = locationTracker,
                                            settingsRepository = settingsRepository
                                        )
                                    )
                                    HomeScreen(viewModel = homeViewModel)
                                }

                                composable(Screen.Places.route) {
                                    val placesViewModel: PlacesViewModel = viewModel(
                                        factory = PlacesViewModel.Factory(
                                            repository = repository,
                                            units      = "metric",
                                            lang       = "en"
                                        )
                                    )
                                    PlacesView(
                                        viewModel         = placesViewModel,
                                        onLocationClick   = { id ->
                                            navController.navigate(Screen.PlaceDetail.createRoute(id))
                                        },
                                        onAddLocationClick = {
                                            mapPickerLauncher.launch(
                                                Intent(context, MapPickerActivity::class.java)
                                            )
                                        }
                                    )
                                }

                                composable(
                                    route = Screen.PlaceDetail.route,
                                    arguments = listOf(
                                        navArgument(Screen.PlaceDetail.ARG_ID) {
                                            type = NavType.IntType
                                        }
                                    )
                                ) { backStackEntry ->
                                    val favouriteId = backStackEntry.arguments
                                        ?.getInt(Screen.PlaceDetail.ARG_ID) ?: return@composable
                                    val detailViewModel: PlaceDetailViewModel = viewModel(
                                        factory = PlaceDetailViewModel.Factory(
                                            favouriteId  = favouriteId,
                                            repository   = repository,
                                            units        = "metric",
                                            lang         = "en"
                                        )
                                    )
                                    PlaceDetailView(
                                        viewModel = detailViewModel,
                                        onBack    = { navController.popBackStack() }
                                    )
                                }

                                composable(Screen.Alerts.route) {
                                    val alertViewModel: AlertViewModel = viewModel(
                                        factory = AlertViewModel.Factory(alertRepository)
                                    )
                                    AlertsScreen(viewModel = alertViewModel)
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