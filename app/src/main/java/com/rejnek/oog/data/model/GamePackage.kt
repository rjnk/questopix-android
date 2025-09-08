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

    fun getName() = gameInfo["name"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Game name not found")

    fun info(key: String) : String{
        return gameInfo[key]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Game info '$key' not found")
    }

    fun infoAsJson(key: String) : JsonObject?{
        return gameInfo[key]?.jsonObject
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
