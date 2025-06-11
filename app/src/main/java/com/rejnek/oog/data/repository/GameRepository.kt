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
    private val jsEngine: JsGameEngine
) {
    private val _currentGame = MutableStateFlow<Game?>(null)
    val currentGame: StateFlow<Game?> = _currentGame.asStateFlow()

    private val _currentElement = MutableStateFlow<GameElement?>(null)
    val currentElement: StateFlow<GameElement?> = _currentElement.asStateFlow()

    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    /**
     * Initialize a game element in JavaScript and evaluate its properties
     */
    suspend fun initializeGameElement() = withContext(Dispatchers.IO) {
        try {
            // Initialize the game in JavaScript environment
            jsEngine.evaluateJs(demoGame)

            // set the start element
            setCurrentElement("start")

        } catch (e: Exception) {
            Log.e("GameRepository", "JavaScript evaluation error: ${e.javaClass.simpleName}", e)
        }
    }

    suspend fun setCurrentElement(elementId: String) {
        val name = jsEngine.evaluateJs("$elementId.name").getOrNull() ?: "Error"
        val elementType = jsEngine.evaluateJs("$elementId.type").getOrNull()?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN
        val description = jsEngine.evaluateJs("$elementId.description").getOrNull() ?: "No description"

        _currentElement.value = GameElement(
            id = elementId,
            name = name,
            elementType = elementType,
            description = description,
            visible = true
        )

        Log.d("GameRepository", "Current element set: ${_currentElement.value}")
    }

    /**
     * Execute onContinue script for a game element
     */
    suspend fun executeOnContinue(element: GameElement?) {
        jsEngine.evaluateJs("${currentElement.value?.id}.onContinue()")
    }
    
    /**
     * Clean up JavaScript engine
     */
    fun cleanup() {
        jsEngine.cleanup()
        _currentGame.value = null
    }
}