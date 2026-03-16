package com.darkzoom.tempsphere.ui.core.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home     : Screen("home",     "Home",     Icons.Rounded.Home)
    object Places   : Screen("places",   "Places",   Icons.Rounded.LocationOn)
    object Alerts   : Screen("alerts",   "Alerts",   Icons.Rounded.Notifications)
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings)

    object PlaceDetail : Screen("place_detail/{favouriteId}", "Place Detail", Icons.Rounded.LocationOn) {
        const val ARG_ID = "favouriteId"
        fun createRoute(id: Int) = "place_detail/$id"
    }
}