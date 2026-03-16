package com.darkzoom.tempsphere.ui.places.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.darkzoom.tempsphere.data.local.model.SearchResult
import com.darkzoom.tempsphere.ui.core.Theme.LocalAppTheme
import com.darkzoom.tempsphere.ui.places.components.BottomConfirmCard
import com.darkzoom.tempsphere.ui.places.components.MapViewContainer
import com.darkzoom.tempsphere.ui.places.components.MyLocationFab
import com.darkzoom.tempsphere.ui.places.components.SearchOverlay
import com.darkzoom.tempsphere.ui.places.viewmodel.MapPickerUiState
import com.darkzoom.tempsphere.ui.places.viewmodel.MapPickerViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

@Composable
 fun MapPickerScreen(
    viewModel: MapPickerViewModel,
    onClose: () -> Unit
) {
    val theme = LocalAppTheme.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var noResults by remember { mutableStateOf<String?>(null) }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(query) {
        noResults = null
        if (query.isBlank() || query.length < 2) {
            results = emptyList()
            isSearching = false
            return@LaunchedEffect
        }
        isSearching = true
        delay(450)

        val found = mutableListOf<SearchResult>()
        withContext(Dispatchers.IO) {
            runCatching {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        suspendCancellableCoroutine { cont ->
                            geocoder.getFromLocationName(query, 8) { addrs -> cont.resume(addrs) }
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocationName(query, 8) ?: emptyList()
                    }

                for (addr in addresses) {
                    if (!addr.hasLatitude() || !addr.hasLongitude()) continue
                    val primary = (addr.locality
                        ?: addr.subAdminArea
                        ?: addr.featureName
                        ?: addr.adminArea
                        ?: query).trim()
                    val secondary = listOfNotNull(
                        addr.adminArea?.takeIf { it != primary },
                        addr.countryName
                    ).distinct().joinToString(", ")
                    found += SearchResult(primary, secondary, addr.latitude, addr.longitude)
                }
            }
        }

        results = found
            .distinctBy { "${String.format("%.2f", it.lat)},${String.format("%.2f", it.lon)}" }
            .take(6)
        isSearching = false
        if (results.isEmpty()) noResults = "No results for \"$query\""
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            runCatching {
                googleMap?.isMyLocationEnabled = true
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 13f)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine && !hasCoarse) {
            permLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    fun pickLocation(lat: Double, lon: Double) {
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(LatLng(lat, lon)).title("Selected"))
        viewModel.onLocationPicked(lat, lon)
    }

    fun requestMyLocation() {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine) {
            runCatching {
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 14f)
                        )
                    }
                }
            }
        } else {
            permLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapViewContainer(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { map ->
                googleMap = map
                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                map.uiSettings.apply {
                    isZoomControlsEnabled = false
                    isCompassEnabled = true
                    isMyLocationButtonEnabled = false
                }

                if (hasFine) {
                    runCatching {
                        map.isMyLocationEnabled = true
                        fusedClient.lastLocation.addOnSuccessListener { loc ->
                            val target = if (loc != null) LatLng(loc.latitude, loc.longitude) else LatLng(30.0444, 31.2357)
                            map.moveCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.fromLatLngZoom(target, if (loc != null) 12f else 5f)
                                )
                            )
                        }
                    }
                } else {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(30.0444, 31.2357), 5f))
                }

                map.setOnMapClickListener { latLng ->
                    keyboard?.hide()
                    query = ""
                    results = emptyList()
                    noResults = null
                    pickLocation(latLng.latitude, latLng.longitude)
                }
            }
        )

        SearchOverlay(
            query = query,
            onQueryChange = { query = it },
            isSearching = isSearching,
            results = results,
            noResults = noResults,
            theme = theme,
            onClose = onClose,
            onClear = {
                query = ""
                results = emptyList()
                noResults = null
            },
            onResultClick = { result ->
                keyboard?.hide()
                query = ""
                results = emptyList()
                noResults = null
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result.lat, result.lon), 11f))
                pickLocation(result.lat, result.lon)
            }
        )

        MyLocationFab(
            theme = theme,
            onClick = { requestMyLocation() }
        )

        AnimatedVisibility(
            visible = uiState is MapPickerUiState.Resolved || uiState is MapPickerUiState.Resolving || uiState is MapPickerUiState.Saving,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomConfirmCard(
                uiState = uiState,
                theme = theme,
                onConfirm = { viewModel.saveCurrentLocation() },
                onDismiss = { viewModel.resetError() }
            )
        }
    }
}