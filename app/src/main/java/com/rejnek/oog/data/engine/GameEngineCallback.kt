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
     * Called when the game engine needs to add a button to the UI
     */
    suspend fun addButton(text: String, onClick: () -> Unit)

    /**
     * Called when the game engine needs to show a question and get user input
     */
    suspend fun showQuestion(questionText: String, provideAnswer: (String) -> Unit)
}
