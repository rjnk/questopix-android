package com.rejnek.oog.data.repository

import android.content.Context
import com.rejnek.oog.data.engine.JsGameEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository responsible for JavaScript game engine operations
 */
class GameEngineRepository(
    context: Context
) {
    private val jsEngine = JsGameEngine(context)

    suspend fun initialize(gameRepository: GameRepository): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext jsEngine.initialize(gameRepository).map { }
    }

    suspend fun initializeGame(gameCode: String) = withContext(Dispatchers.IO) {
        jsEngine.evaluateJs(gameCode)
    }

    suspend fun executeOnStart(taskId: String) = withContext(Dispatchers.IO) {
        jsEngine.executeOnStart(taskId)
    }

    suspend fun executeOnEnter(taskId: String) = withContext(Dispatchers.IO) {
        jsEngine.executeOnEnter(taskId)
    }

    suspend fun getJsValue(id: String): String? = withContext(Dispatchers.IO) {
        jsEngine.getJsValue(id).getOrNull()
    }

    suspend fun evaluateJs(code: String) = withContext(Dispatchers.IO) {
        jsEngine.evaluateJs(code)
    }

    fun cleanup() {
        jsEngine.cleanup()
    }
}
