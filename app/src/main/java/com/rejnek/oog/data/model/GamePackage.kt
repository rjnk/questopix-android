package com.rejnek.oog.data.model

import android.util.Log
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class GamePackage(
    val gameInfo: JsonObject,
    val gameCode: String,
    var state: GameState,
    val importedAt: Long,
    var currentTaskId: String = "start",
    val gameState: JsonObject? = null
    ) {
    fun getId() = gameInfo["id"]?.jsonPrimitive?.content ?: throw IllegalStateException("Game ID not found")

    fun getName() = gameInfo["name"]?.jsonPrimitive?.content ?: throw IllegalStateException("Game name not found")

    fun info(key: String) : String{
        return gameInfo[key]?.jsonPrimitive?.content ?: throw IllegalStateException("Game info '$key' not found")
    }
}

@Serializable
enum class GameState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
)

@Serializable
data class Area(
    val points: List<Coordinates>,
)
