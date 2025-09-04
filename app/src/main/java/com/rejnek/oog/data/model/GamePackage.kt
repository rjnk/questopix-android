package com.rejnek.oog.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
class GamePackage(
    val gameInfo: JsonObject,
    val gameCode: String,
    val importedAt: Long,
) {
    fun getId() = gameInfo["id"]?.jsonPrimitive?.content ?: "ERROR"
    fun getName() = gameInfo["name"]?.jsonPrimitive?.content ?: "ERROR"

    fun info(key: String) : String{
        return gameInfo[key]?.jsonPrimitive?.content ?: "ERROR"
    }
}
