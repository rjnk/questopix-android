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
    private val jsEngine = JavaScriptGameEngine(this)
    
    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()
    
    /**
     * Execute onContinue script for a game element
     */
    suspend fun executeOnContinue(element: GameElement?): Result<Unit> {
        val element = element ?: _currentGame.value?.currentElement

        Log.d("GameRepository", "Executing onContinue for element: ${element?.name}")

        // Execute the onContinue script in the JavaScript engine
        val result = jsEngine.executeOnContinue(element)

        // Return the result
        return result
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

            // Get all available elements from the JavaScript scope
            val availableElements = jsEngine.getAllElementsFromScope()

            elements.addAll(availableElements)
            Log.d("GameRepository", "Found ${availableElements.size} game elements")


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

    fun setCurrentElement(string: String) {
        val currentGame = _currentGame.value ?: return
        val nextElement = currentGame.elements.find { it.id == string } ?: return

        // Update the current element and index
        _currentGame.value = currentGame.copy(
            currentElement = nextElement,
            currentElementIndex = currentGame.elements.indexOf(nextElement)
        )

        Log.d("GameRepository", "Current element set to: ${nextElement.name}")
    }
}