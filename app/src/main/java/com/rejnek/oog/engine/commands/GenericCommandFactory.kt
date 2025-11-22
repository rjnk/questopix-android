package com.rejnek.oog.engine.commands

import com.rejnek.oog.engine.JsGameInterface
import com.rejnek.oog.data.repository.GameRepository

/**
 * Abstract class representing a generic command factory.
 *
 * @property gameRepository The game repository instance
 * @property game The JavaScript game interface instance
 */
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