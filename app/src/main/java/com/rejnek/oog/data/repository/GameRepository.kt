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
import com.rejnek.oog.data.gameItems.ButtonFactory
import com.rejnek.oog.data.gameItems.DebugPrint
import com.rejnek.oog.data.gameItems.GenericGameFactory
import com.rejnek.oog.data.gameItems.HeadingFactory
import com.rejnek.oog.data.gameItems.QuestionFactory
import com.rejnek.oog.data.gameItems.ShowTask
import com.rejnek.oog.data.gameItems.TextFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    context: Context
) {
    val gameItems = arrayListOf<GenericGameFactory>(
        DebugPrint(),
        QuestionFactory(),
        ShowTask(),
        ButtonFactory(),
        TextFactory(),
        HeadingFactory()
    )

    val jsEngine = JsGameEngine(context)

    private val _currentElement = MutableStateFlow(
        GameElement(
            id = "initial",
            name = "Loading...",
            elementType = GameElementType.ERROR,
            visible = true
        )
    )
    val currentElement: StateFlow<GameElement> = _currentElement.asStateFlow()

    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()


    suspend fun initializeGame() = withContext(Dispatchers.IO) {
        jsEngine.evaluateJs(demoGame) // Load the demo js code
        setCurrentElement("start")
    }

    suspend fun setCurrentElement(elementId: String) {
        val name = getJsValue("$elementId.name") ?: "Err"
        val elementType = getJsValue("$elementId.type")?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN

        _currentElement.value = GameElement(
            id = elementId,
            name = name,
            elementType = elementType,
            visible = true
        )

        if( elementType != GameElementType.FINISH && elementType != GameElementType.START ) {
            _uiElements.value = emptyList()
        }

        Log.d("GameRepository", "Current element set to ${_currentElement.value}")

        executeOnStart()
    }

    private suspend fun getJsValue(id: String): String? {
        return jsEngine.getJsValue(id).getOrNull()
    }

    /**
     * Execute the onStart method of the current game element in JavaScript
     * This is called when the element is set or when the game starts
     */
    suspend fun executeOnStart() {
        val elementId = currentElement.value.id
        jsEngine.evaluateJs("$elementId.onStart()")
    }

    /**
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     */
    fun addUIElement(element: @Composable () -> Unit) {
        _uiElements.value = _uiElements.value + element
    }

    fun cleanup() {
        jsEngine.cleanup()
    }
}