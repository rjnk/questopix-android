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
import com.rejnek.oog.data.gameItems.direct.factory.DistanceFactory
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
        MapFactory(),
    )

    // JavaScript game engine
    val jsEngine = JsGameEngine(context)

    // User location
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    // Current task location monitoring scope
    private val locationMonitoringScope = CoroutineScope(Dispatchers.IO)

    // Need to save --------------------------------------------------------------------------------
    // If the game type is Branching - the current element is the visible one
    private val _currentElement: MutableStateFlow<GameElement?> = MutableStateFlow(null)
    val currentElement: StateFlow<GameElement?> = _currentElement.asStateFlow()

    // ---------------------------------------------------------------------------------------------

    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()

    suspend fun initializeGame() = withContext(Dispatchers.IO) {
        jsEngine.evaluateJs(demoGame) // Load the demo js code
        setCurrentElement("start")
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
        Log.d("GameRepository", "Getting game element with ID: $id")

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

    suspend fun getGameType(): GameType {
        val typeString = jsEngine.getJsValue("gameType").getOrNull() ?: return GameType.UNKNOWN
        return GameType.valueOf(typeString.uppercase())
    }

    suspend fun getVisibleElements(): List<GameElement> {
        val ids = jsEngine
            .getJsValue("visibleTasks")
            .getOrNull()
            ?.removeSurrounding("[", "]")
            ?.replace("\"", "")
            ?.split(",")
            ?.map { it.trim() }
            ?: return emptyList()

        Log.d("GameRepository", "Visible elements IDs: $ids")

        return ids.map { id -> getGameElement(id) }
    }

    fun cleanup() {
        jsEngine.cleanup()
        _uiElements.value = emptyList()
    }

    private suspend fun getJsValue(id: String): String? {
        return jsEngine.getJsValue(id).getOrNull()
    }
}