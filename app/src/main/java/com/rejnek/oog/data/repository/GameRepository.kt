package com.rejnek.oog.data.repository

import com.rejnek.oog.data.engine.JsEngineInterface
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import com.rejnek.oog.data.engine.demoGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    val jsEngine: JsEngineInterface
) : GameRepositoryInterface {
    private val _currentElement = MutableStateFlow(
        GameElement(
            id = "initial",
            name = "Loading...",
            elementType = GameElementType.UNKNOWN,
            description = "Please wait while game is loading...",
            visible = true
        )
    )
    val currentElement: StateFlow<GameElement> = _currentElement.asStateFlow()

    // Changed from a single button state to a list of buttons
    private val _buttons = MutableStateFlow<List<ButtonState>>(emptyList())
    val buttons: StateFlow<List<ButtonState>> = _buttons.asStateFlow()

    // Add state for questions
    private val _questionState = MutableStateFlow<QuestionState?>(null)
    val questionState: StateFlow<QuestionState?> = _questionState.asStateFlow()

    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    /**
     * Initialize a game element in JavaScript and evaluate its properties
     */
    suspend fun initializeGameElement() = withContext(Dispatchers.IO) {
        try {

            // Then execute the game code using executeJs which properly maintains state
            // This defines all game elements in the persistent JS context
            jsEngine.evaluateJs(demoGame)

            // Set the start element
            setCurrentElement("start")

        } catch (e: Exception) {
            Log.e("GameRepository", "JavaScript evaluation error: ${e.javaClass.simpleName}", e)
        }
    }

    suspend fun setCurrentElement(elementId: String) {
        val name = jsEngine.getJsValue("$elementId.name").getOrNull() ?: "Error"
        val elementType = jsEngine.getJsValue("$elementId.type").getOrNull()?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN
        val description = jsEngine.getJsValue("$elementId.description").getOrNull() ?: "No description"

        _currentElement.value = GameElement(
            id = elementId,
            name = name,
            elementType = elementType,
            description = description,
            visible = true
        )

        // Clear buttons when changing elements
        _buttons.value = emptyList()

        // Clear any active question
        _questionState.value = null

        Log.d("GameRepository", "Current element set: ${_currentElement.value}")

        executeOnStart()
    }

    /**
     * Execute onContinue script for a game element
     */
    suspend fun executeOnContinue(element: GameElement?) {
        val elementId = currentElement.value.id
        jsEngine.evaluateJs("$elementId.onContinue()")
    }

    suspend fun executeOnStart() {
        val elementId = currentElement.value.id
        jsEngine.evaluateJs("$elementId.onStart()")
    }
    
    /**
     * Execute a specific button's action
     */
    suspend fun executeButtonAction(buttonId: Int) {
        val buttons = _buttons.value
        if (buttonId >= 0 && buttonId < buttons.size) {
            // Execute the callback for this button
            buttons[buttonId].onClick.invoke()
        }
    }

    /**
     * Submit answer to a question
     */
    suspend fun submitAnswer(answer: String) {
        _questionState.value?.provideAnswer(answer)
    }

    /**
     * Clean up JavaScript engine
     */
    fun cleanup() {
        jsEngine.cleanup()
    }

    /**
     * Implementation of GameEngineCallback interface
     */
    override suspend fun showTask(elementId: String) {
        setCurrentElement(elementId)
    }

    override suspend fun addButton(text: String, onClick: () -> Unit) {
        Log.d("GameRepository", "Adding button: $text")
        // Add the new button to the list
        _buttons.value = _buttons.value + ButtonState(text, onClick)
    }

    override suspend fun showQuestion(questionText: String, provideAnswer: (String) -> Unit) {
        Log.d("GameRepository", "Showing question: $questionText")
        _questionState.value = QuestionState(questionText, provideAnswer)
    }

    /**
     * Class representing state of a button created from JavaScript
     */
    data class ButtonState(
        val text: String,
        val onClick: () -> Unit
    )

    /**
     * Class representing a question that needs to be answered
     */
    data class QuestionState(
        val questionText: String,
        val provideAnswer: (String) -> Unit
    )
}