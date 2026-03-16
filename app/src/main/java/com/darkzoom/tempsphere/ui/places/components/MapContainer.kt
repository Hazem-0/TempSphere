package com.darkzoom.tempsphere.ui.places.components

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

@Composable
 fun MapViewContainer(modifier: Modifier = Modifier, onMapReady: (GoogleMap) -> Unit) {
    val mapViewState = remember { mutableStateOf<MapView?>(null) }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).also { mv ->
                mv.onCreate(Bundle())
                mv.getMapAsync { map -> onMapReady(map) }
                mapViewState.value = mv
            }
        },
        modifier = modifier
    )

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            val mv = mapViewState.value ?: return@LifecycleEventObserver
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START -> mv.onStart()
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mv.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mv.onPause()
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> mv.onStop()
                androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mv.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}