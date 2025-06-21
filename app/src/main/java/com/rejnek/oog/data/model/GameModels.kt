package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.atan2
import kotlin.math.PI

@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double,
    val radius: Double
)

@Serializable
class GameElement(
    val id: String,
    val name: String,
    val elementType: GameElementType,
    val coordinates: Coordinates? = null,
    var visible: Boolean = false,
){
    fun isInside(userLat: Double, userLng: Double): Boolean{
        if (coordinates == null) return false

        // Haversine formula to calculate distance in meters
        val earthRadius = 6371000.0 // Earth radius in meters
        val latDistance = Math.toRadians(userLat - coordinates.lat)
        val lngDistance = Math.toRadians(userLng - coordinates.lng)

        val a = sin(latDistance / 2).pow(2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(coordinates.lat)) *
                sin(lngDistance / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c  // Distance in meters

        return distance <= coordinates.radius
    }
}

enum class GameElementType {
    ERROR,
    UNKNOWN,
    START,
    TASK,
    FINISH,
}
