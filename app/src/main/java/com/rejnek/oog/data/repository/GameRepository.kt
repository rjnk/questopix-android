package com.rejnek.oog.data.repository

import android.content.Context
import com.rejnek.oog.data.engine.JsGameEngine
import com.rejnek.oog.data.model.Game
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import com.rejnek.oog.data.engine.demoGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.Result

class GameRepository(
    private val context: Context,
    private val jsEngine: JsGameEngine
) {
    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()

    /**
     * Evaluate a sample JavaScript calculation
     */
    suspend fun eval(): Result<String> {
        return jsEngine.calculateExample(500)
    }

    /**
     * Initialize a game element in JavaScript and evaluate its properties
     */
    suspend fun initializeGameElement(): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Initialize the element in JavaScript environment
            val initResult = jsEngine.evaluateJs(demoGame)
            if (initResult.isFailure) {
                return@withContext Result.failure(initResult.exceptionOrNull() ?: Exception("Failed to initialize game element"))
            }

            // Get the name property from the initialized element
            val nameResult = jsEngine.evaluateJs("nav1.name")
            Log.d("GameRepository", "JavaScript name property: ${nameResult.getOrNull()}")

            // Get the description property
            val descriptionResult = jsEngine.evaluateJs("start.description")
            Log.d("GameRepository", "JavaScript description property: ${descriptionResult.getOrNull()}")

            return@withContext nameResult
        } catch (e: Exception) {
            Log.e("GameRepository", "JavaScript evaluation error: ${e.javaClass.simpleName}", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Get the name property from the JavaScript environment
     */
    suspend fun evalName(): Result<String> {
        return jsEngine.evaluateJs("start.name")
    }

    /**
     * Execute onContinue script for a game element
     */
    suspend fun executeOnContinue(element: GameElement?): Result<Unit> {
        // This will be implemented later using the jsEngine to call the element's onContinue function
        throw UnsupportedOperationException("JavaScript engine execution not implemented yet")
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
    suspend fun loadGameFromAsset(context: Context, assetFileName: String): Result<Game> {
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
            // Initialize the JavaScript engine if not already initialized
            val initResult = jsEngine.initialize()
            if (initResult.isFailure) {
                Log.e("GameRepository", "Failed to initialize JavaScript engine",
                      initResult.exceptionOrNull() ?: Exception("Unknown error"))
            }

            // Extract game elements from JavaScript objects
            val elements = mutableListOf<GameElement>()

            // Test JavaScript engine with a sample calculation
            val jsResult = eval()
            Log.d("GameRepository", "JS eval result: ${jsResult.getOrNull() ?: "failed"}")

            // Initialize a game element for testing
            val elementResult = initializeGameElement()
            Log.d("GameRepository", "Game element initialization: ${elementResult.getOrNull() ?: "failed"}")

            // Create game even if JS fails - implement fallback behavior
            val game = Game(
                elements = elements,
                gameType = "linear",
                currentElement = elements.firstOrNull { it.elementType == GameElementType.START } ?: elements.firstOrNull(),
                currentElementIndex = 0,
            )

            _currentGame.value = game
            Result.success(game)
        } catch (e: Exception) {
            Log.e("GameRepository", "Error loading game from JavaScript", e)
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