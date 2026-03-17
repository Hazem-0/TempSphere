package com.darkzoom.tempsphere.ui.core

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
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
import com.darkzoom.tempsphere.ui.core.components.BottomNavBar
import com.darkzoom.tempsphere.ui.core.components.Screen
import com.darkzoom.tempsphere.ui.home.HomeScreen
import com.darkzoom.tempsphere.ui.home.HomeViewModel
import com.darkzoom.tempsphere.ui.places.MapPickerActivity
import com.darkzoom.tempsphere.ui.places.MapPickerActivity.Companion.EXTRA_MODE
import com.darkzoom.tempsphere.ui.places.MapPickerActivity.Companion.MODE_PLACES
import com.darkzoom.tempsphere.ui.places.MapPickerActivity.Companion.MODE_SETTINGS
import com.darkzoom.tempsphere.ui.places.MapPickerActivity.Companion.RESULT_PLACE_SAVED
import com.darkzoom.tempsphere.ui.places.view.PlaceDetailView
import com.darkzoom.tempsphere.ui.places.view.PlacesView
import com.darkzoom.tempsphere.ui.places.viewmodel.PlaceDetailViewModel
import com.darkzoom.tempsphere.ui.places.viewmodel.PlacesViewModel
import com.darkzoom.tempsphere.ui.settings.SettingsEvent
import com.darkzoom.tempsphere.ui.settings.SettingsScreen
import com.darkzoom.tempsphere.ui.settings.SettingsViewModel
import com.darkzoom.tempsphere.ui.settings.SettingsViewModelFactory
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme
import com.darkzoom.tempsphere.utils.LocaleHelper
import com.darkzoom.tempsphere.utils.toApiLang
import com.darkzoom.tempsphere.utils.toApiUnits
import kotlinx.coroutines.delay
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(base: Context) {
        val prefs    = base.getSharedPreferences("tempsphere_preferences", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "English") ?: "English"
        super.attachBaseContext(LocaleHelper.wrap(base, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val app                = application as App
        val repository         = app.repository
        val locationTracker    = app.locationTracker
        val settingsRepository = app.settingsRepository
        val alertRepository    = app.alertRepository

        setContent {
            val context       = LocalContext.current
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute  = navBackStackEntry?.destination?.route
            val showBottomBar = currentRoute != Screen.PlaceDetail.route
            var currentHour by remember {
                mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            }
            LaunchedEffect(Unit) {
                while (true) {
                    val secsLeft = 60 - Calendar.getInstance().get(Calendar.SECOND)
                    delay(secsLeft * 1_000L)
                    currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                }
            }

            val timeSlot = when (currentHour) {
                in 6..11  -> "morning"
                in 12..17 -> "afternoon"
                else      -> "night"
            }

            val appTheme = when (timeSlot) {
                "morning"   -> MorningColors
                "afternoon" -> AfternoonColors
                else        -> NightColors
            }

            val currentLanguage by settingsRepository.languageFlow.collectAsState()
            val layoutDirection = if (currentLanguage == "Arabic")
                LayoutDirection.Rtl else LayoutDirection.Ltr

            val placesMapLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_PLACE_SAVED) {
                    navController.navigate(Screen.Places.route) { launchSingleTop = true }
                }
            }

            val settingsMapLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {  }

            TempSphereTheme(darkTheme = true) {
                CompositionLocalProvider(
                    LocalAppTheme        provides appTheme,
                    LocalLayoutDirection provides layoutDirection
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        Crossfade(
                            targetState   = timeSlot,
                            animationSpec = tween(durationMillis = 1_500),
                            label         = "background_crossfade"
                        ) { slot ->
                            when (slot) {
                                "morning"   -> MorningBackground()
                                "afternoon" -> AfterNoonBackground()
                                else        -> NightBackground()
                            }
                        }

                        Scaffold(
                            bottomBar = {
                                if (showBottomBar) BottomNavBar(navController = navController)
                            },
                            containerColor      = androidx.compose.ui.graphics.Color.Transparent,
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
                                            units      = settingsRepository.tempUnit.toApiUnits(),
                                            lang       = settingsRepository.language.toApiLang(),
                                            app
                                        )
                                    )
                                    PlacesView(
                                        viewModel          = placesViewModel,
                                        onLocationClick    = { id ->
                                            navController.navigate(Screen.PlaceDetail.createRoute(id))
                                        },
                                        onAddLocationClick = {
                                            placesMapLauncher.launch(
                                                Intent(context, MapPickerActivity::class.java).apply {
                                                    putExtra(EXTRA_MODE, MODE_PLACES)
                                                }
                                            )
                                        }
                                    )
                                }

                                composable(
                                    route     = Screen.PlaceDetail.route,
                                    arguments = listOf(
                                        navArgument(Screen.PlaceDetail.ARG_ID) {
                                            type = NavType.IntType
                                        }
                                    )
                                ) { backStackEntry ->
                                    val favouriteId = backStackEntry.arguments
                                        ?.getInt(Screen.PlaceDetail.ARG_ID)
                                        ?: return@composable
                                    val detailViewModel: PlaceDetailViewModel = viewModel(
                                        factory = PlaceDetailViewModel.Factory(
                                            favouriteId = favouriteId,
                                            repository  = repository,
                                            units       = settingsRepository.tempUnit.toApiUnits(),
                                            lang        = settingsRepository.language.toApiLang(),
                                            context     = app
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
                                        factory = SettingsViewModelFactory(app)
                                    )

                                    LaunchedEffect(settingsViewModel) {
                                        settingsViewModel.events.collect { event ->
                                            when (event) {
                                                is SettingsEvent.OpenMapPicker -> {
                                                    settingsMapLauncher.launch(
                                                        Intent(app, MapPickerActivity::class.java).apply {
                                                            putExtra(EXTRA_MODE, MODE_SETTINGS)
                                                        }
                                                    )
                                                }
                                                is SettingsEvent.RestartActivity -> {

                                                    recreate()
                                                }
                                            }
                                        }
                                    }

                                    SettingsScreen(
                                        viewModel       = settingsViewModel,
                                        onOpenMapPicker = {
                                            settingsMapLauncher.launch(
                                                Intent(app, MapPickerActivity::class.java).apply {
                                                    putExtra(EXTRA_MODE, MODE_SETTINGS)
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

