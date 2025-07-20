package com.rejnek.oog.data.repository

import android.content.Context
import com.rejnek.oog.data.model.GameTask
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
import com.rejnek.oog.data.gameItems.GenericItemFactory
import com.rejnek.oog.data.gameItems.direct.factory.HeadingFactory
import com.rejnek.oog.data.gameItems.callback.QuestionFactory
import com.rejnek.oog.data.gameItems.direct.commands.Refresh
import com.rejnek.oog.data.gameItems.direct.commands.Save
import com.rejnek.oog.data.gameItems.direct.factory.DistanceFactory
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
    val gameItems = arrayListOf<GenericItemFactory>(
        DebugPrint(),
        QuestionFactory(),
        ButtonFactory(),
        TextFactory(),
        HeadingFactory(),
        DistanceFactory(),
        MapFactory(),
        Refresh(),
        Save()
    )

    // JavaScript game engine
    val jsEngine = JsGameEngine(context)

    // User location
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    // Current task location monitoring scope
    private val locationMonitoringScope = CoroutineScope(Dispatchers.IO)

    private val _selectedElement: MutableStateFlow<GameTask?> = MutableStateFlow(null)
    val selectedElement: StateFlow<GameTask?> = _selectedElement.asStateFlow()

    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()

    suspend fun initializeGame() = withContext(Dispatchers.IO) {
        jsEngine.evaluateJs(demoGame) // Load the demo js code
        selectTask("start")
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

    suspend fun refresh(){
        selectTask(getCurrentTaskId())
    }

    suspend fun selectTask(elementId: String) {
        _selectedElement.value = getTask(elementId)

        val elementType = _selectedElement.value?.elementType
        if( elementType != GameElementType.FINISH && elementType != GameElementType.START ) {
            _uiElements.value = emptyList()
        }

        Log.d("GameRepository", "Selected element set to ${_selectedElement.value?.id}")

        if( checkLocation() ) {
            executeOnEnter()
        }
        else {
            executeOnStart()
        }
    }

    suspend fun getTask(id: String): GameTask {
        Log.d("GameRepository", "Getting game element with ID: $id")

        val name = getJsValue("$id.name") ?: "Err"
        val elementType = getJsValue("$id.type")?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN
        val coordinates = jsEngine.getCoordinates(id)

        return GameTask(
            id = id,
            name = name,
            elementType = elementType,
            coordinates = coordinates,
            visible = true
        )
    }

    fun checkLocation(): Boolean {
        return selectedElement.value?.isInside(currentLocation.value.first, currentLocation.value.second) == true
    }

    suspend fun executeOnStart() {
        val elementId = selectedElement.value?.id
        jsEngine.evaluateJs("$elementId.onStart()")
    }

    suspend fun executeOnEnter() {
        val elementId = selectedElement.value?.id
        jsEngine.evaluateJs("$elementId.onEnter()")
    }

    /**
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     */
    fun addUIElement(element: @Composable () -> Unit) {
        _uiElements.value = _uiElements.value + element
    }

    suspend fun getCurrentTaskId() : String {
        return getJsValue("_currentTask") ?: ""
    }

    suspend fun getSecondaryTabElementId() : String {
        return getJsValue("_secondaryTask") ?: ""
    }

    suspend fun getGameType(): GameType {
        val typeString = getJsValue("_gameType") ?: return GameType.UNKNOWN
        return GameType.valueOf(typeString.uppercase())
    }

    suspend fun getVisibleElements(): List<GameTask> {
        val ids = jsEngine
            .getJsValue("_visibleTasks")
            .getOrNull()
            ?.removeSurrounding("[", "]")
            ?.replace("\"", "")
            ?.split(",")
            ?.map { it.trim() }
            ?: return emptyList()

        Log.d("GameRepository", "Visible elements IDs: $ids")

        return ids.map { id -> getTask(id) }
    }

    fun cleanup() {
        jsEngine.cleanup()
        _uiElements.value = emptyList()
    }

    private suspend fun getJsValue(id: String): String? {
        return jsEngine.getJsValue(id).getOrNull()
    }
}