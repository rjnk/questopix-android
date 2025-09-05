package com.rejnek.oog.data.gameItems.direct.factory

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.rejnek.oog.data.gameItems.GenericDirectFactory

class DistanceFactory : GenericDirectFactory() {
    override val id = "distance"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.addUIElement {
            Distance(
                gameRepository?.gameLocationRepository?.currentLocation?.collectAsState(),
                gameRepository?.gameLocationRepository?.currentLocation?.collectAsState() // TODO
            ).Show()
        }
    }
}

class Distance(
    private val currentLocation: State<Pair<Double, Double>>?,
    private val currentElement: State<Pair<Double, Double>>?
) {
    @Composable
    fun Show() {
        val location = currentLocation?.value
        if (location == null) {
            Text("Location unavailable")
            return
        }

        val lastDistance = remember { mutableStateOf<Int?>(null) }
        val distance = 10 // TODO: Calculate distance based on location and currentElement

        if (distance != null) {
            lastDistance.value = distance
        }

        lastDistance.value?.let {
            Text("Zbyva: ${it}m")
        } ?: Text("Distance unavailable")
    }
}