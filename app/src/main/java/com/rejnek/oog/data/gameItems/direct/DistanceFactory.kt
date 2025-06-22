package com.rejnek.oog.data.gameItems.direct

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.data.model.GameElement

class DistanceFactory : GenericDirectFactory() {
    override val id = "distance"

    override suspend fun create(data: String, callbackId: String) {
        gameRepository?.addUIElement {
            Distance(
                gameRepository?.currentLocation?.collectAsState(),
                gameRepository?.currentElement?.collectAsState()
            ).Show()
        }
    }
}

class Distance(
    private val currentLocation: State<Pair<Double, Double>>?,
    private val currentElement: State<GameElement?>?
) {
    @Composable
    fun Show() {
        val location = currentLocation?.value
        if (location != null) {
            Text(
                text = "Zbyva: ${
                    currentElement?.value?.calculateDistance(
                        currentLocation.value.first,
                        currentLocation.value.second
                    )?.toInt()
                }m",
            )
        } else {
            Text(
                text = "Location unavailable",
            )
        }
    }
}