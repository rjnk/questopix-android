package com.rejnek.oog.data.repository

import android.content.Context
import com.rejnek.oog.data.model.Game
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.javascriptengine.JavaScriptIsolate
import androidx.javascriptengine.JavaScriptSandbox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.Result
import kotlin.text.get

class GameRepository(
    val context: Context
) {

    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()

    // Make eval a suspend function that runs on IO dispatcher
    suspend fun eval(): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("GameRepository", "Initializing JavaScript engine on background thread...")

            // Try with timeout using coroutines
            val jsSandbox = withTimeoutOrNull(15000L) {
                val jsSandboxFuture = JavaScriptSandbox.createConnectedInstanceAsync(context)
                jsSandboxFuture.get()
            } ?: throw TimeoutException("JavaScript sandbox initialization timed out")

            Log.d("GameRepository", "JavaScript sandbox initialized successfully")

            // Create isolate and evaluate code
            jsSandbox.createIsolate().use { jsIsolate ->
                Log.d("GameRepository", "JavaScript isolate created")

                val n = 500

                val code = "function sum(a, b) { return (a * b).toString(); }; sum($n, 6)"
                val result = jsIsolate.evaluateJavaScriptAsync(code).get(5, TimeUnit.SECONDS)

                Log.d("GameRepository", "JavaScript evaluation result: $result")

                val task1 = """
                    const start = {
                        name: "Hra v divočině",
                        type: "start",
                        gameType: "linear",
                        coordinates: {
                            lat: 50.0815,
                            lng: 14.3980,
                            radius: 25
                        },
                        description: "Tohle je jednoduchá demonstrační hra pro účely vyzkoušení načítání z javascriptu.",
                        onContinue: function() {
                            consolePrint("CONSOLE PRINT: game is starting!");
                            showElement("nav1");
                        }
                    }
                """.trimIndent()
                jsIsolate.evaluateJavaScriptAsync(task1).get(5, TimeUnit.SECONDS)

                val result2 = jsIsolate.evaluateJavaScriptAsync("start.name").get(5, TimeUnit.SECONDS)
                Log.d("GameRepository", "JavaScript name property: $result2")

                val descripion = jsIsolate.evaluateJavaScriptAsync("start.description").get(5, TimeUnit.SECONDS)
                Log.d("GameRepository", "JavaScript description property: $descripion")

                return@withContext Result.success(result)
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "JavaScript evaluation error: ${e.javaClass.simpleName}", e)

            // Provide a fallback value when JavaScript fails
            return@withContext Result.failure(e)
        }
    }

    suspend fun evalName(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val jsSandbox = withTimeoutOrNull(15000L) {
                val jsSandboxFuture = JavaScriptSandbox.createConnectedInstanceAsync(context)
                jsSandboxFuture.get()
            } ?: throw TimeoutException("JavaScript sandbox initialization timed out")



            jsSandbox.createIsolate().use { jsIsolate ->
                // First, evaluate the code that defines the start object

                // Then extract the name property
                val result = jsIsolate.evaluateJavaScriptAsync("start.name").get(5, TimeUnit.SECONDS)
                Log.d("GameRepository", "JavaScript name property: $result")
                return@withContext Result.success(result)
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "JavaScript evaluation error: ${e.javaClass.simpleName}", e)
            return@withContext Result.failure(e)
        }
    }

    /**
     * Execute onContinue script for a game element
     */
    suspend fun executeOnContinue(element: GameElement?): Result<Unit> {
        // Implementation commented out for now
        throw UnsupportedOperationException("JavaScript engine execution not implemented yet")
    }
    
    /**
     * Clean up JavaScript engine
     */
    fun cleanup() {
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

    // Update loadGameFromJavaScript to use the new eval function
    suspend fun loadGameFromJavaScript(gameScript: String): Result<Game> {
        return try {
            // Extract game elements from JavaScript objects
            val elements = mutableListOf<GameElement>()

            // Test JavaScript engine
            val jsResult = eval()
            Log.d("GameRepository", "JS eval result: ${jsResult.getOrNull() ?: "failed"}")

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