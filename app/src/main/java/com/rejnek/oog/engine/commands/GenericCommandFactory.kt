package com.rejnek.oog.engine.commands

import com.rejnek.oog.engine.JsGameInterface
import com.rejnek.oog.data.repository.GameRepository

abstract class GenericCommandFactory(
    var gameRepository: GameRepository? = null,
    var game: JsGameInterface? = null,
) {
    abstract val id: String
    abstract val js: String

    fun init(gameRepository: GameRepository?, game: JsGameInterface?) {
        this.gameRepository = gameRepository
        this.game = game
    }
}