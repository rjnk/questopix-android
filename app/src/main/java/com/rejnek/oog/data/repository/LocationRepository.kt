package com.rejnek.oog.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.rejnek.oog.data.model.Area
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.services.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Repository responsible for location-based game operations
 */
class LocationRepository(
    private val context: Context
) {
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    private val _permissionGranted = MutableStateFlow(checkPermission())
    val isPermissionGranted = _permissionGranted.asStateFlow()

    fun startLocationService() {
        refreshPermissionStatus()
        if(!_permissionGranted.value) {
            Log.d("LocationRepository", "Location permission not granted, cannot start location service.")
            return
        }

        locationService.startLocationUpdates()
    }

    fun startMonitoringAreas(
        areas: List<Area>,
        onLocationMatch: suspend (String) -> Unit
    ) {
        if(areas.isEmpty()) {
            Log.d("LocationRepository", "No areas to monitor.")
            return
        }

        startLocationService()

        Log.d("LocationRepository", "Starting location monitoring for areas: $areas")

        CoroutineScope(Dispatchers.Main).launch {
            currentLocation.collectLatest { location ->
                if(location == null) {
                    Log.d("LocationRepository", "Current location is null, skipping check.")
                    return@collectLatest
                }

                areas.forEach { area ->
                    if (checkLocation(location, area)) {
                        onLocationMatch(area.id)
                    }
                }
            }
        }
    }

    fun refreshPermissionStatus() {
        _permissionGranted.value = checkPermission()
    }

    fun checkPermission() : Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun stopLocationMonitoring() {
        locationService.stopLocationUpdates()
    }

    fun checkLocation(location: Coordinates, areaToCheck: Area): Boolean {
        // Simple point-in-polygon check using ray casting algorithm
        if (areaToCheck.points.size < 3) return false // Need at least 3 points for a polygon

        var isInside = false
        val x = location.lng
        val y = location.lat

        for (i in areaToCheck.points.indices) {
            val j = if (i == 0) areaToCheck.points.size - 1 else i - 1
            val xi = areaToCheck.points[i].lng
            val yi = areaToCheck.points[i].lat
            val xj = areaToCheck.points[j].lng
            val yj = areaToCheck.points[j].lat

            if (((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                isInside = !isInside
            }
        }

        return isInside
    }

    companion object {
        /* Haversine formula to calculate distance between two coordinates in meters
        * from: Coordinates of the first point
        * to: Coordinates of the second point
        * returns: Distance in meters
         */
        fun calculateDistance(from: Coordinates, to: Coordinates): Double {
            val earthRadius = 6371000.0
            val latDistance = Math.toRadians(to.lat - from.lat)
            val lngDistance = Math.toRadians(to.lng - from.lng)

            val a = sin(latDistance / 2).pow(2) +
                    cos(Math.toRadians(from.lat)) * cos(Math.toRadians(to.lat)) *
                    sin(lngDistance / 2).pow(2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return earthRadius * c
        }
    }
}
