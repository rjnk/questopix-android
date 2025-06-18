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
)

enum class GameElementType {
    ERROR,
    UNKNOWN,
    START,
    TASK,
    FINISH,
}
