package com.rejnek.oog.data.engine.gameItems

import com.rejnek.oog.data.engine.JsGameEngine
import com.rejnek.oog.data.repository.GameRepositoryInterface

abstract class GenericGameItem(
    var gameRepository: GameRepositoryInterface? = null,
    var game: JsGameEngine.GameJsInterface? = null,
) {
    abstract val id: String
    abstract val js: String

    fun init(gameRepository: GameRepositoryInterface?, game: JsGameEngine.GameJsInterface?) {
        this.gameRepository = gameRepository
        this.game = game
    }

    abstract suspend fun run(data: String, callbackId: String)
}