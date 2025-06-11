package com.rejnek.oog.data.engine

/**
 * Callback interface for the game engine to communicate with the game logic
 * This breaks the circular dependency between JsGameEngine and GameRepository
 */
interface GameEngineCallback {
    /**
     * Called when the game engine needs to show a specific element
     */
    suspend fun showElement(elementId: String)

    /**
     * Debug logging from the JavaScript environment
     */
    fun debugLog(message: String)
}
