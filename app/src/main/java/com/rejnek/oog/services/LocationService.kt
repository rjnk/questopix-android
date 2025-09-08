package com.rejnek.oog.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper.getMainLooper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rejnek.oog.data.model.Coordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationService(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val _currentLocation: MutableStateFlow<Coordinates?> = MutableStateFlow(null)
    val currentLocation: StateFlow<Coordinates?> = _currentLocation

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.value = Coordinates(location.latitude, location.longitude)
            }
        }
    }

    fun startLocationUpdates() {
        // Simple permission check – if neither fine nor coarse permission is granted, abort.
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!fineGranted) {
            Log.w("LocationService", "Missing location permission; not starting updates")
            return
        }

        Log.d("LocationService", "Starting location updates in LocationService")

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1500 // interval in milliseconds
        ).apply {
            setMinUpdateDistanceMeters(25f) // smallest displacement
            setWaitForAccurateLocation(true)
        }.build()

        Log.d("LocationService", "Requesting location updates with interval: 1500ms, min distance: 25m")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        Log.d("LocationService", "Stopping location updates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}