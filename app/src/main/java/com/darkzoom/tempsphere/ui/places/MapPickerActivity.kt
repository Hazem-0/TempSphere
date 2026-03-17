package com.darkzoom.tempsphere.ui.places

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.darkzoom.tempsphere.ui.core.App
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.core.Theme.NightColors
import com.darkzoom.tempsphere.ui.places.view.MapPickerScreen
import com.darkzoom.tempsphere.ui.places.viewmodel.MapPickerUiState
import com.darkzoom.tempsphere.ui.places.viewmodel.MapPickerViewModel
import com.darkzoom.tempsphere.ui.theme.TempSphereTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapPickerActivity : ComponentActivity() {

    companion object {
        const val RESULT_PLACE_SAVED       = 100
        const val RESULT_HOME_LOCATION_SET = 101

        const val EXTRA_MODE      = "map_picker_mode"
        const val MODE_PLACES     = "places"
        const val MODE_SETTINGS   = "settings"
    }

    private val viewModel: MapPickerViewModel by viewModels {
        MapPickerViewModel.Factory((application as App).repository , application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        val mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_PLACES

        val composeView = ComposeView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                TempSphereTheme(darkTheme = true) {
                    CompositionLocalProvider(LocalAppTheme provides NightColors) {
                        MapPickerScreen(
                            viewModel  = viewModel,
                            onClose    = { finish() },
                            onConfirm  = {
                                if (mode == MODE_SETTINGS) viewModel.saveAsHomeLocation()
                                else                       viewModel.saveCurrentLocation()
                            }
                        )
                    }
                }
            }
        }
        setContentView(composeView)

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when {
                    state is MapPickerUiState.SavedSuccess -> {
                        setResult(RESULT_PLACE_SAVED)
                        finish()
                    }
                    state is MapPickerUiState.HomeLocationSaved -> {
                        val resolved = viewModel.lastResolvedLocation
                        if (resolved != null) {
                            (application as App).settingsRepository
                                .saveMapLocation(resolved.first, resolved.second)
                        }
                        setResult(RESULT_HOME_LOCATION_SET)
                        finish()
                    }
                }
            }
        }
    }
}