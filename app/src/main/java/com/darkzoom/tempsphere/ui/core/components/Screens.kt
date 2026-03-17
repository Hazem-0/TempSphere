package com.darkzoom.tempsphere.ui.core.components

import com.darkzoom.tempsphere.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val titleRes: Int, val icon: ImageVector) {

    object Home : Screen("home", R.string.home, Icons.Rounded.Home)
    object Places : Screen("places", R.string.places, Icons.Rounded.LocationOn)
    object Alerts : Screen("alerts", R.string.alerts, Icons.Rounded.Notifications)
    object Settings : Screen("settings", R.string.settings, Icons.Rounded.Settings)

    object PlaceDetail : Screen("place_detail/{favouriteId}", R.string.place_detail, Icons.Rounded.LocationOn) {
        const val ARG_ID = "favouriteId"
        fun createRoute(id: Int) = "place_detail/$id"
    }
}