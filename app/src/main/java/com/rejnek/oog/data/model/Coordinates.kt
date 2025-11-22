package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonPrimitive

/**
 * Represents a geographic location with latitude and longitude.
 *
 * Provides formatted string output (e.g., "50.12345N, 14.67890E").
 */
@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
) {
    companion object {
        /** Parses coordinates from a JSON object with "lat" and "lng" fields. */
        fun fromJson(json: JsonObject): Coordinates? {
            val lat = json["lat"]?.jsonPrimitive?.double ?: return null
            val lng = json["lng"]?.jsonPrimitive?.double ?: return null
            return Coordinates(lat, lng)
        }
    }

    /**
     * Returns coordinates in human-readable format with N/S and E/W suffixes.
     * example: "50.12345N, 14.67890E"
     */
    override fun toString(): String {
        val latDirection = if (lat >= 0) "N" else "S"
        val lngDirection = if (lng >= 0) "E" else "W"
        val formattedLat = "%.5f".format(kotlin.math.abs(lat))
        val formattedLng = "%.5f".format(kotlin.math.abs(lng))
        return "$formattedLat$latDirection, $formattedLng$lngDirection"
    }
}
