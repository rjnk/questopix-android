package com.rejnek.oog.data.repository

import com.rejnek.oog.data.engine.JavaScriptGameEngine
import com.rejnek.oog.data.model.Game
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

class GameRepository {
    private val jsEngine = JavaScriptGameEngine(
        this
    )
    
    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()
    
    /**
     * Load a game from JavaScript code
     */
    suspend fun loadGameFromJavaScript(gameScript: String): Result<Game> {
        return try {

            // Initialize JavaScript engine with the game script
            val initResult = jsEngine.initialize(gameScript)
            if (initResult.isFailure) {
                return Result.failure(initResult.exceptionOrNull()!!)
            }
            
            // Extract game elements from JavaScript objects
            val elements = mutableListOf<GameElement>()

            // Try to extract common element names (start, nav1, task1, etc.)
            val elementNames = listOf("start", "nav1", "nav2", "task1", "task2", "task3", "finish")

            for (elementName in elementNames) {
                jsEngine.getElementFromScope(elementName)?.let { element ->
                    elements.add(element)
                }
            }
            
            if (elements.isEmpty()) {
                return Result.failure(Exception("No game elements found in JavaScript"))
            }

            val game = Game(
                elements = elements,
                gameType = "linear",

                currentElement = elements.firstOrNull { it.elementType == GameElementType.START } ?: elements.first(),
                currentElementIndex = 0,
                )
            
            _currentGame.value = game
            
            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Execute onContinue script for a game element
     */
    suspend fun executeOnContinue(element: GameElement?): Result<Unit> {
        val game = _currentGame.value ?: return Result.failure(Exception("No game context"))
        // val result = jsEngine.executeOnContinue(element, game)

        Log.d("GameRepository", "Execute on continue mock")

        _currentGame.value?.let { game ->
            val nextIndex = game.currentElementIndex + 1
            val nextElement = game.elements.getOrNull(nextIndex) ?: game.currentElement
            _currentGame.value = game.copy(
                currentElementIndex = nextIndex,
                currentElement = nextElement
            )
        }

//        // After executing the onContinue script, check if the game state was updated by the JavaScript code
//        jsEngine.getCurrentGameRef()?.let { updatedGame ->
//            if (updatedGame.currentElement != game.currentElement) {
//                _currentGame.value = updatedGame
//            }
//        }

        return Result.success(Unit)
    }
    
    /**
     * Clean up JavaScript engine
     */
    fun cleanup() {
        jsEngine.cleanup()
        _currentGame.value = null
    }
    
    /**
     * Load a game from an asset file
     */
    suspend fun loadGameFromAsset(context: android.content.Context, assetFileName: String): Result<Game> {
        return try {
            val inputStream = context.assets.open(assetFileName)
            val gameScript = inputStream.bufferedReader().use { it.readText() }
            loadGameFromJavaScript(gameScript)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}