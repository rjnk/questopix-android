package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class GamePackage(
    val gameInfo: JsonObject,
    val gameCode: String,
    var state: GameState,
    val importedAt: Long,
) {
    fun getId() = gameInfo["id"]?.jsonPrimitive?.content ?: "ERROR"
    fun getName() = gameInfo["name"]?.jsonPrimitive?.content ?: "ERROR"

    fun info(key: String) : String{
        return gameInfo[key]?.jsonPrimitive?.content ?: "ERROR"
    }
}

@Serializable
enum class GameState {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}
