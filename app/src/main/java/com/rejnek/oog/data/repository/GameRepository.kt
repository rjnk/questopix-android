package com.rejnek.oog.data.repository

import android.content.Context
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.compose.runtime.Composable
import com.rejnek.oog.data.engine.JsGameEngine
import com.rejnek.oog.data.engine.demoGame
import com.rejnek.oog.data.engine.gameItems.DebugPrint
import com.rejnek.oog.data.engine.gameItems.GenericGameItem
import com.rejnek.oog.data.engine.gameItems.InGameButton
import com.rejnek.oog.data.engine.gameItems.Question
import com.rejnek.oog.data.engine.gameItems.ShowTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    context: Context
) {
    val jsEngine = JsGameEngine(context)

    val gameItems = arrayListOf<GenericGameItem>(
        DebugPrint(),
        Question(),
        InGameButton(),
        ShowTask()
    )

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

    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    // UI elements storage - list of composable functions
    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()

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

        // Clear UI elements when changing elements
        _uiElements.value = emptyList()

        // Clear any active question
        // TODO this problematic as this is too fast and the screen flickers
        for (item in gameItems) {
            item.clear()
        }

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
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     */
    fun addUIElement(element: @Composable () -> Unit) {
        Log.d("GameRepository", "Adding UI element to screen")
        _uiElements.value = _uiElements.value + element
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
    suspend fun showTask(elementId: String) {
        setCurrentElement(elementId)
    }

    suspend fun addButton(text: String, onClick: () -> Unit) {
        Log.d("GameRepository", "Adding button: $text")
        // Add the new button to the list
        _buttons.value = _buttons.value + ButtonState(text, onClick)
    }

    /**
     * Class representing state of a button created from JavaScript
     */
    data class ButtonState(
        val text: String,
        val onClick: () -> Unit
    )
}