package com.rejnek.oog.services

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper.getMainLooper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationService(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation: MutableStateFlow<Pair<Double, Double>> = MutableStateFlow(Pair(0.0, 0.0)) // TODO strange default IG
    val currentLocation: StateFlow<Pair<Double, Double>> = _currentLocation

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.value = Pair(location.latitude, location.longitude)
            }
        }
    }

    init {
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2500 // interval in milliseconds
        ).apply {
            setMinUpdateDistanceMeters(25f) // smallest displacement
            setWaitForAccurateLocation(true)
        }.build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}