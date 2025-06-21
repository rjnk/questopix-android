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
import com.rejnek.oog.data.gameItems.direct.DebugPrint
import com.rejnek.oog.data.gameItems.GenericGameFactory
import com.rejnek.oog.data.gameItems.direct.HeadingFactory
import com.rejnek.oog.data.gameItems.callback.QuestionFactory
import com.rejnek.oog.data.gameItems.direct.ShowTask
import com.rejnek.oog.data.gameItems.direct.TextFactory
import com.rejnek.oog.data.model.Coordinates
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
        HeadingFactory()
    )

    val jsEngine = JsGameEngine(context)

    // Initialize LocationService to track user's position
    private val locationService = LocationService(context)

    // Current user location accessible as a pair of (latitude, longitude)
    val currentLocation = locationService.currentLocation

    // Current task location monitoring scope
    private val locationMonitoringScope = CoroutineScope(Dispatchers.IO)

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
        val name = getJsValue("$elementId.name") ?: "Err"
        val elementType = getJsValue("$elementId.type")?.let {
            GameElementType.valueOf(it.toString().uppercase())
        } ?: GameElementType.UNKNOWN
        val coordinates = jsEngine.getCoordinates(elementId)

        _currentElement.value = GameElement(
            id = elementId,
            name = name,
            elementType = elementType,
            coordinates = coordinates,
            visible = true
        )

        if( elementType != GameElementType.FINISH && elementType != GameElementType.START ) {
            _uiElements.value = emptyList()
        }

        Log.d("GameRepository", "Current element set to ${_currentElement.value}")

        if( checkLocation() ) {
            executeOnEnter()
        }
        else {
            executeOnStart()
        }
    }

    suspend fun checkLocation(): Boolean {
        return currentElement.value.isInside(currentLocation.value.first, currentLocation.value.second)
    }

    suspend fun executeOnStart() {
        val elementId = currentElement.value.id
        jsEngine.evaluateJs("$elementId.onStart()")
    }

    suspend fun executeOnEnter() {
        val elementId = currentElement.value.id
        jsEngine.evaluateJs("$elementId.onEnter()")
    }

    /**
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     */
    fun addUIElement(element: @Composable () -> Unit) {
        _uiElements.value = _uiElements.value + element
    }

    fun cleanup() {
        jsEngine.cleanup()
        // locationService.stopLocationUpdates()
    }

    private suspend fun getJsValue(id: String): String? {
        return jsEngine.getJsValue(id).getOrNull()
    }
}