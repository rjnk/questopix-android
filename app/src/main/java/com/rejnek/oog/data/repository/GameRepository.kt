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
import com.rejnek.oog.data.engine.gameItems.ButtonFactory
import com.rejnek.oog.data.engine.gameItems.DebugPrint
import com.rejnek.oog.data.engine.gameItems.GenericGameItem
import com.rejnek.oog.data.engine.gameItems.HeadingFactory
import com.rejnek.oog.data.engine.gameItems.Question
import com.rejnek.oog.data.engine.gameItems.QuestionFactory
import com.rejnek.oog.data.engine.gameItems.ShowTask
import com.rejnek.oog.data.engine.gameItems.TextFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    context: Context
) {
    val jsEngine = JsGameEngine(context)

    val gameItems = arrayListOf<GenericGameItem>(
        DebugPrint(),
        QuestionFactory(),
        ShowTask(),
        ButtonFactory(),
        TextFactory(),
        HeadingFactory()
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
        var clear = true

        val name = jsEngine.getJsValue("$elementId.name").getOrNull() ?: "Error"
        val elementType = jsEngine.getJsValue("$elementId.type").getOrNull()?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN
        val description = jsEngine.getJsValue("$elementId.description").getOrNull() ?: "No description"

        if( elementType == GameElementType.FINISH || elementType == GameElementType.START ) {
            clear = false
        }

        _currentElement.value = GameElement(
            id = elementId,
            name = name,
            elementType = elementType,
            description = description,
            visible = true
        )

        if( clear ) {
            _uiElements.value = emptyList()

            for (item in gameItems) {
                item.clear()
            }
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
}