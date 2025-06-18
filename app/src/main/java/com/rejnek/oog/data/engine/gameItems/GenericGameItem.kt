package com.rejnek.oog.data.engine.gameItems

import com.rejnek.oog.data.engine.JsGameInterface
import com.rejnek.oog.data.repository.GameRepository

abstract class GenericGameItem(
    var gameRepository: GameRepository? = null,
    var game: JsGameInterface? = null,
) {
    abstract val id: String
    abstract val js: String

    fun init(gameRepository: GameRepository?, game: JsGameInterface?) {
        this.gameRepository = gameRepository
        this.game = game
    }

    abstract suspend fun run(data: String, callbackId: String)

    abstract fun clear()
}