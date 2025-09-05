package com.rejnek.oog.data.repository

import android.content.Context
import android.util.Log
import com.rejnek.oog.services.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    fun startLocationMonitoring(onLocationMatch: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            currentLocation.collectLatest { location ->
                Log.d("GameLocationRepository", "Location updated: $location")
                if (checkLocation()) {
                    onLocationMatch()
                }
            }
        }
    }

    fun checkLocation(): Boolean {
        // TODO: Implement actual location checking logic
        // val coordinates = jsEngine.getCoordinates(currentTaskId)
        return false
    }
}
