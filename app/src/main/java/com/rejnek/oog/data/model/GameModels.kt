package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double,
    val radius: Double = 25.0
)

@Serializable
data class GameElement(
    val id: String,
    val name: String,
    val elementType: GameElementType,
    var visible: Boolean = false,
    val coordinates: Coordinates,
    val description: String,
    val onContinueScript: String? = null,
)

enum class GameElementType {
    START,
    NAVIGATION,
    TASK
}

@Serializable
data class Game(
    val elements: List<GameElement>,
    val gameType: String? = null,
    val currentElement: GameElement
)
