package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
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
    fun getId() = gameInfo["id"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Game ID not found")

    fun getName() = gameInfo["name"]?.jsonPrimitive?.content ?: "No name"
    fun getDescription() = gameInfo["description"]?.jsonPrimitive?.content ?: "No description available"
    fun getCoverPhoto() = gameInfo["coverPhoto"]?.jsonPrimitive?.content ?: ""

    // Start and Finish locations
    fun getStartLocationText(): String? {
        return gameInfo["startLocation"]?.jsonObject["text"]?.jsonPrimitive?.content
    }
    fun getStartLocation(): Coordinates? {
        val startLocationJson = gameInfo["startLocation"]?.jsonObject ?: return null
        return Coordinates.fromJson(startLocationJson["coordinates"]?.jsonObject ?: return null)
    }
    fun getFinishLocationText(): String? {
        return gameInfo["finishLocation"]?.jsonObject["text"]?.jsonPrimitive?.content
    }
    fun getFinishLocation(): Coordinates? {
        val finishLocationJson = gameInfo["finishLocation"]?.jsonObject ?: return null
        return Coordinates.fromJson(finishLocationJson["coordinates"]?.jsonObject ?: return null)
    }

    fun getTaskIds(): List<String> {
        val taskPattern = Regex("""const\s+(\w+)\s*=\s*\{""")
        return taskPattern.findAll(gameCode)
            .map { it.groupValues[1] }
            .toList()
    }
}

@Serializable
enum class GameState {
    NOT_STARTED,
    IN_PROGRESS,
    FINISHED,
    ARCHIVED
}

@Serializable
data class Coordinates(
    val lat: Double,
    val lng: Double
){
    override fun toString(): String {
        val latDirection = if (lat >= 0) "N" else "S"
        val lngDirection = if (lng >= 0) "E" else "W"
        val formattedLat = "%.5f".format(kotlin.math.abs(lat))
        val formattedLng = "%.5f".format(kotlin.math.abs(lng))
        return "$formattedLat$latDirection, $formattedLng$lngDirection"
    }

    companion object {
        fun fromJson(json: JsonObject): Coordinates? {
            val lat = json["lat"]?.jsonPrimitive?.double ?: return null
            val lng = json["lng"]?.jsonPrimitive?.double ?: return null
            return Coordinates(lat, lng)
        }
    }
}

@Serializable
data class Area(
    val points: List<Coordinates>,
    val id: String
)
