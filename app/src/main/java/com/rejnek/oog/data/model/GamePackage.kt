package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Represents a game package with metadata, code, and runtime state.
 *
 * Contains the game's info.json data, JavaScript code, current state,
 * and task progress for save/resume functionality.
 */
@Serializable
data class GamePackage(
    val gameInfo: JsonObject,
    val gameCode: String,
    var state: GameState,
    val importedAt: Long,
    var currentTaskId: String = "start",
    val gameState: JsonObject? = null
) {
    fun getId() = gameInfo["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Game ID not found")

    fun getName() = gameInfo["name"]?.jsonPrimitive?.content ?: "No name"
    fun getDescription() = gameInfo["description"]?.jsonPrimitive?.content ?: "No description available"
    fun getCoverPhoto() = gameInfo["coverPhoto"]?.jsonPrimitive?.content ?: ""

    /** Returns human-readable text for start location, if defined. */
    fun getStartLocationText(): String? {
        return gameInfo["startLocation"]?.jsonObject["text"]?.jsonPrimitive?.content
    }

    /** Parses start location coordinates from game info. */
    fun getStartLocation(): Coordinates? {
        val startLocationJson = gameInfo["startLocation"]?.jsonObject ?: return null
        return Coordinates.fromJson(startLocationJson["coordinates"]?.jsonObject ?: return null)
    }

    /** Returns human-readable text for finish location, if defined. */
    fun getFinishLocationText(): String? {
        return gameInfo["finishLocation"]?.jsonObject["text"]?.jsonPrimitive?.content
    }

    /** Parses finish location coordinates from game info. */
    fun getFinishLocation(): Coordinates? {
        val finishLocationJson = gameInfo["finishLocation"]?.jsonObject ?: return null
        return Coordinates.fromJson(finishLocationJson["coordinates"]?.jsonObject ?: return null)
    }

    /** Extracts task IDs from JavaScript game code using regex. */
    fun getTaskIds(): List<String> {
        val taskPattern = Regex("""const\s+(\w+)\s*=\s*\{""")
        return taskPattern.findAll(gameCode)
            .map { it.groupValues[1] }
            .toList()
    }
}
