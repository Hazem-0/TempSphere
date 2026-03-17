package com.darkzoom.tempsphere.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

interface LocationUtil {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
    fun hasLocationPermission(): Boolean
    fun hasFineLocationPermission(): Boolean
    fun checkSettings(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}

class LocationUtilImp(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationUtil {

    override fun hasLocationPermission(): Boolean =
        hasFineLocationPermission() || hasCoarseLocationPermission()

    override fun hasFineLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            application, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasCoarseLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            application, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) return null
        val fresh = getFreshLocation()
        if (fresh != null) return fresh
        return getLastKnownLocation()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getFreshLocation(): Pair<Double, Double>? {
        return suspendCancellableCoroutine { cont ->
            val cts = CancellationTokenSource()
            cont.invokeOnCancellation { cts.cancel() }

            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cts.token
            ).addOnSuccessListener { location ->
                cont.resume(
                    if (location != null) Pair(location.latitude, location.longitude)
                    else null
                )
            }.addOnFailureListener {
                cont.resume(null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastKnownLocation(): Pair<Double, Double>? {
        return try {
            val location = locationClient.lastLocation.await()
            if (location != null) Pair(location.latitude, location.longitude)
            else null
        } catch (e: Exception) {
            null
        }
    }

    override fun checkSettings(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request)
        val task    = LocationServices.getSettingsClient(application)
            .checkLocationSettings(builder.build())

        task.addOnSuccessListener { onSuccess() }
        task.addOnFailureListener { onFailure(it) }
    }
}