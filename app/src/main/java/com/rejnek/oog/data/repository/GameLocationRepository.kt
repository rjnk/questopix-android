package com.rejnek.oog.data.repository

import android.content.Context
import android.util.Log
import com.rejnek.oog.data.model.Area
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.services.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Repository responsible for location-based game operations
 */
class GameLocationRepository(
    context: Context
) {
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    fun startLocationMonitoring(
        areas: List<Area>,
        onLocationMatch: suspend (String) -> Unit
    ) {
        Log.d("GameLocationRepository", "Starting location monitoring for areas: $areas")

        CoroutineScope(Dispatchers.Main).launch {
            currentLocation.collectLatest { location ->
                Log.d("GameLocationRepository", "Location updated: $location")
                areas.forEach { area ->
                    if (checkLocation(location, area)) {
                        onLocationMatch(area.id ?: throw IllegalStateException("Area ID is null"))
                    }
                }
            }
        }
    }

    fun checkLocation(areaToCheck: Area): Boolean {
        val location = currentLocation.value
        return checkLocation(location, areaToCheck)
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
