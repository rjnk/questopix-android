package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a geographic area defined by polygon points.
 *
 * Used for location-based task boundaries in games.
 */
@Serializable
data class Area(
    val points: List<Coordinates>,
    val id: String
)
