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
    val description: String,

    var visible: Boolean = false,
)

enum class GameElementType {
    UNKNOWN,
    START,
    NAVIGATION,
    TASK,
    FINISH,
    TASK2
}

@Serializable
data class Game(
    val elements: List<GameElement>,
    val gameType: String? = null,

    val currentElement: GameElement?,
    val currentElementIndex: Int
)
