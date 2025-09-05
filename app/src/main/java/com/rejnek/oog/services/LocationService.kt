package com.rejnek.oog.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper.getMainLooper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rejnek.oog.data.model.Coordinates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationService(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val _currentLocation: MutableStateFlow<Coordinates> = MutableStateFlow(Coordinates(0.0, 0.0)) // TODO strange default IG
    val currentLocation: StateFlow<Coordinates> = _currentLocation

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.value = Coordinates(location.latitude, location.longitude)
            }
        }
    }

    init {
        Log.d("LocationService", "LocationService initialized, starting location updates")
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
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