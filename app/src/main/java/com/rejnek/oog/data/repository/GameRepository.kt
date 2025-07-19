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
import com.rejnek.oog.data.gameItems.callback.ButtonFactory
import com.rejnek.oog.data.gameItems.direct.commands.DebugPrint
import com.rejnek.oog.data.gameItems.GenericGameFactory
import com.rejnek.oog.data.gameItems.direct.factory.HeadingFactory
import com.rejnek.oog.data.gameItems.callback.QuestionFactory
import com.rejnek.oog.data.gameItems.direct.commands.SetGameType
import com.rejnek.oog.data.gameItems.direct.factory.DistanceFactory
import com.rejnek.oog.data.gameItems.direct.commands.SetHidden
import com.rejnek.oog.data.gameItems.direct.commands.SetVisible
import com.rejnek.oog.data.gameItems.direct.commands.ShowTask
import com.rejnek.oog.data.gameItems.direct.factory.TextFactory
import com.rejnek.oog.data.gameItems.direct.factory.map.MapFactory
import com.rejnek.oog.data.model.GameType
import com.rejnek.oog.services.LocationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
        HeadingFactory(),
        DistanceFactory(),
        SetVisible(),
        SetHidden(),
        MapFactory(),
        SetGameType()
    )

    // JavaScript game engine
    val jsEngine = JsGameEngine(context)

    // User location
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    // Current task location monitoring scope
    private val locationMonitoringScope = CoroutineScope(Dispatchers.IO)

    // Need to save --------------------------------------------------------------------------------
    private val _gameType = MutableStateFlow<GameType>(GameType.UNKNOWN)
    val gameType: StateFlow<GameType> = _gameType.asStateFlow()

    // If the game type is Branching - the current element is the visible one
    private val _currentElement: MutableStateFlow<GameElement?> = MutableStateFlow(null)
    val currentElement: StateFlow<GameElement?> = _currentElement.asStateFlow()

    // If the game type is Open, this is the list of all visible elements
    private val _visibleElements = MutableStateFlow<List<GameElement>>(emptyList())
    val visibleElements: StateFlow<List<GameElement>> = _visibleElements.asStateFlow()
    // ---------------------------------------------------------------------------------------------

    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()

    suspend fun initializeGame() = withContext(Dispatchers.IO) {
        jsEngine.evaluateJs(demoGame) // Load the demo js code
        setCurrentElement("start")
        setElementVisible("start", true)
        startLocationMonitoring()
    }

    private fun startLocationMonitoring() {
        locationMonitoringScope.launch {
            currentLocation.collectLatest { location ->
                Log.d("GameRepository", "Location updated: $location")
                if( checkLocation() ){
                    executeOnEnter()
                }
            }
        }
    }

    suspend fun setCurrentElement(elementId: String) {
        _currentElement.value = getGameElement(elementId)

        val elementType = _currentElement.value?.elementType
        if( elementType != GameElementType.FINISH && elementType != GameElementType.START ) {
            _uiElements.value = emptyList()
        }

        Log.d("GameRepository", "Current element set to ${_currentElement.value?.id}")

        if( checkLocation() ) {
            executeOnEnter()
        }
        else {
            executeOnStart()
        }
    }

    suspend fun getGameElement(id: String): GameElement {
        val name = getJsValue("$id.name") ?: "Err"
        val elementType = getJsValue("$id.type")?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN
        val coordinates = jsEngine.getCoordinates(id)

        return GameElement(
            id = id,
            name = name,
            elementType = elementType,
            coordinates = coordinates,
            visible = true
        )
    }

    suspend fun setElementVisible(elementId: String, visible: Boolean) {
        Log.d("GameRepository", "Setting element $elementId visibility to $visible")

        if (visible) {
            if (!_visibleElements.value.any { it.id == elementId }) {
                val elementToAdd = getGameElement(elementId)
                _visibleElements.value = _visibleElements.value + elementToAdd
            }
        } else {
            val newList = _visibleElements.value.filter { it.id != elementId }
            _visibleElements.value = newList

            if( _currentElement.value?.id == elementId ) {
                Log.d("GameRepository", "Current element $elementId is no longer visible, resetting current element")
                _currentElement.value = null
            }
        }
    }

    fun checkLocation(): Boolean {
        return currentElement.value?.isInside(currentLocation.value.first, currentLocation.value.second) == true
    }

    suspend fun executeOnStart() {
        val elementId = currentElement.value?.id
        jsEngine.evaluateJs("$elementId.onStart()")
    }

    suspend fun executeOnEnter() {
        val elementId = currentElement.value?.id
        jsEngine.evaluateJs("$elementId.onEnter()")
    }

    /**
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     */
    fun addUIElement(element: @Composable () -> Unit) {
        _uiElements.value = _uiElements.value + element
    }

    suspend fun getSecondaryTabElementId() : String {
        return jsEngine.getJsValue("secondaryTask").getOrNull() ?: ""
    }

    fun setGameType(type: GameType) {
        _gameType.value = type
    }

    fun cleanup() {
        jsEngine.cleanup()
        _uiElements.value = emptyList()
        _visibleElements.value = emptyList()
    }

    private suspend fun getJsValue(id: String): String? {
        return jsEngine.getJsValue(id).getOrNull()
    }
}