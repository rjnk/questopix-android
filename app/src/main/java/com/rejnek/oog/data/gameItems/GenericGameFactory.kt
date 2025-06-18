package com.rejnek.oog.data.gameItems

import com.rejnek.oog.data.engine.JsGameInterface
import com.rejnek.oog.data.repository.GameRepository

abstract class GenericGameFactory(
    var gameRepository: GameRepository? = null,
    var game: JsGameInterface? = null,
) {
    abstract val id: String
    abstract val js: String

    fun init(gameRepository: GameRepository?, game: JsGameInterface?) {
        this.gameRepository = gameRepository
        this.game = game
    }

    abstract suspend fun create(data: String, callbackId: String)
}