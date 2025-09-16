package com.rejnek.oog.engine.commands.direct.factory

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejnek.oog.R
import com.rejnek.oog.engine.commands.GenericDirectFactory
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.data.repository.LocationRepository
import kotlin.math.*

class DistanceFactory : GenericDirectFactory() {
    override val id = "distance"

    override suspend fun createWithArgs(args: List<String>) {
        val targetLocation = Coordinates(args[0].toDouble(), args[1].toDouble())

        gameRepository?.addUIElement {
            DistanceCard(
                currentLocationState = gameRepository?.locationRepository?.currentLocation?.collectAsState(),
                targetLocation = targetLocation,
            )
        }
    }
}

@Composable
fun DistanceCard(
    currentLocationState: State<Coordinates?>?,
    targetLocation: Coordinates,
    modifier: Modifier = Modifier
) {
    val currentLocation = currentLocationState?.value

    val distanceText = if (currentLocation != null) {
        val distance = LocationRepository.calculateDistance(currentLocation, targetLocation)
        stringResource(R.string.distance_format, formatDistance(distance))
    } else stringResource(R.string.getting_location)

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
                contentDescription = stringResource(R.string.cd_distance),
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

private fun formatDistance(distanceInMeters: Double): String {
    return when {
        distanceInMeters < 1000 -> "${distanceInMeters.roundToInt()}m"
        else -> "${(distanceInMeters / 1000 * 10).roundToInt() / 10.0}km"
    }
}
