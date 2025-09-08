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

/**
 * Repository responsible for location-based game operations
 */
class GameLocationRepository(
    private val context: Context
) {
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    private val _permissionGranted = MutableStateFlow(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    )
    val isPermissionGranted = _permissionGranted.asStateFlow()

    fun setUpLocationMonitoring(
        areas: List<Area>,
        onLocationMatch: suspend (String) -> Unit
    ) {
        if(areas.isEmpty()) {
            Log.d("GameLocationRepository", "No areas to monitor.")
            _permissionGranted.value = true
            return
        }

        checkPermission()
        if(!_permissionGranted.value) {
            Log.d("GameLocationRepository", "Location permission not granted, cannot start monitoring.")
            return
        }

        locationService.startLocationUpdates()
        Log.d("GameLocationRepository", "Starting location monitoring for areas: $areas")

        CoroutineScope(Dispatchers.Main).launch {
            currentLocation.collectLatest { location ->
                if(location == null) {
                    Log.d("GameLocationRepository", "Current location is null, skipping check.")
                    return@collectLatest
                }

                Log.d("GameLocationRepository", "Location updated: $location")
                areas.forEach { area ->
                    if (checkLocation(location, area)) {
                        onLocationMatch(area.id)
                    }
                }
            }
        }
    }

    fun checkPermission() {
        _permissionGranted.value =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun stopLocationMonitoring() {
        locationService.stopLocationUpdates()
    }

    fun checkLocation(locationToCheck: Coordinates, radiusInMeters: Double): Boolean {
        // todo reuse logic in checkLocation and imput a square area around the point with the given radius
        return true
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
}
