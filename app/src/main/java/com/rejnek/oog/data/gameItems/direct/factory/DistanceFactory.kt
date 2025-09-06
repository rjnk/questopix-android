package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.data.model.Coordinates
import kotlin.math.*

class DistanceFactory : GenericDirectFactory() {
    override val id = "distance"

    override suspend fun createWithArgs(args: List<String>) {
        val targetLocation = Coordinates(args[0].toDouble(), args[1].toDouble())

        gameRepository?.addUIElement {
            DistanceCard(
                currentLocationState = gameRepository?.gameLocationRepository?.currentLocation?.collectAsState(),
                targetLocation = targetLocation
            )
        }
    }
}

@Composable
fun DistanceCard(
    currentLocationState: State<Coordinates>?,
    targetLocation: Coordinates,
    modifier: Modifier = Modifier
) {
    val currentLocation = currentLocationState?.value
    val distanceText = if (currentLocation != null) {
        val distance = calculateDistance(currentLocation, targetLocation)
        "Distance: " + formatDistance(distance)
    } else "Getting location..."

    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Distance",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = distanceText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Helper distance functions
private fun calculateDistance(from: Coordinates, to: Coordinates): Double {
    val earthRadius = 6371000.0
    val latDistance = Math.toRadians(to.lat - from.lat)
    val lngDistance = Math.toRadians(to.lng - from.lng)

    val a = sin(latDistance / 2).pow(2) +
            cos(Math.toRadians(from.lat)) * cos(Math.toRadians(to.lat)) *
            sin(lngDistance / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

private fun formatDistance(distanceInMeters: Double): String {
    return when {
        distanceInMeters < 1000 -> "${distanceInMeters.roundToInt()}m"
        else -> "${(distanceInMeters / 1000 * 10).roundToInt() / 10.0}km"
    }
}
