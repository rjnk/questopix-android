package com.rejnek.oog.data.gameItems.direct.commands

import android.util.Log
import com.rejnek.oog.data.gameItems.GenericDirectFactory
import com.rejnek.oog.data.model.GameType

class SetGameType : GenericDirectFactory() {
    override val id = "setGameType"

    override suspend fun create(data: String, callbackId: String) {
        val lowerData = data.lowercase()

        var gameType = when {
            lowerData == "open" -> GameType.OPEN
            lowerData == "branching" -> GameType.BRANCHING
            lowerData == "linear" -> GameType.LINEAR
            else -> GameType.UNKNOWN
        }

        gameRepository?.setGameType(gameType)
    }
}