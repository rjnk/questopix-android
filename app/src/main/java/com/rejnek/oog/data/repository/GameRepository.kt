package com.rejnek.oog.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
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
import com.rejnek.oog.data.gameItems.direct.factory.FinishGameButtonFactory
import com.rejnek.oog.data.gameItems.direct.factory.ImageFactory
import com.rejnek.oog.data.gameItems.direct.factory.TextFactory
import com.rejnek.oog.data.gameItems.direct.factory.map.MapFactory
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.services.LocationService
import com.rejnek.oog.data.storage.GameStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
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
        ImageFactory(),
        FinishGameButtonFactory(),
        MapFactory(),
        Refresh(),
        Save()
    )

    // JavaScript game engine
    val jsEngine = JsGameEngine(context)

    // Game storage for saving/loading
    private val gameStorage = GameStorage(context)

    // Current Game Package & task
    private val _currentGamePackage = MutableStateFlow<GamePackage?>(null)
    val currentGamePackage = _currentGamePackage.asStateFlow()

    // User location
    private val locationService = LocationService(context)
    val currentLocation = locationService.currentLocation

    // UI elements that are rendered for the current task
    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()

    suspend fun initializeGameFromLibrary(gameId: String) = withContext(Dispatchers.IO) {
        val game = gameStorage.getGameById(gameId)
        if (game != null) {
            _currentGamePackage.value = game

            jsEngine.evaluateJs(game.gameCode)
            setCurrentTask("start")
            startLocationMonitoring()
        }
    }

    fun addGameToLibrary(gamePackage: GamePackage) {
        gameStorage.addGameToLibrary(gamePackage)
    }

    fun removeGameFromLibrary(gameId: String) {
        gameStorage.removeGameFromLibrary(gameId)
    }

    fun getLibraryGames() = gameStorage.getLibraryGames()

    private fun startLocationMonitoring() {
        CoroutineScope(Dispatchers.IO).launch {
            currentLocation.collectLatest { location ->
                Log.d("GameRepository", "Location updated: $location")
                if( checkLocation() ){
                    executeOnEnter()
                }
            }
        }
    }

    suspend fun refresh(){
        setCurrentTask(getJsValue("_currentTask") ?: "")
    }

    suspend fun setCurrentTask(elementId: String) {
        _currentGamePackage.value?.currentTaskId = elementId
        _uiElements.value = emptyList()

        Log.d("GameRepository", "Selected element set to $elementId")

        if( checkLocation() ) executeOnEnter()
        else executeOnStart()
    }

    suspend fun checkLocation(): Boolean {
        // val coordinates = jsEngine.getCoordinates(currentGamePackage.value?.currentTaskId ?: "start") // TODO
        // TODO
        return false
    }

    suspend fun executeOnStart() {
        jsEngine.executeOnStart(_currentGamePackage.value?.currentTaskId ?: throw IllegalStateException("Current task ID is null"))
    }

    suspend fun executeOnEnter() {
        jsEngine.executeOnEnter(_currentGamePackage.value?.currentTaskId ?: throw IllegalStateException("Current task ID is null"))
    }

    /**
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     */
    fun addUIElement(element: @Composable () -> Unit) {
        _uiElements.value = _uiElements.value + element
    }

    fun finishGame() {
        _currentGamePackage.value?.let { currentPackage ->
            _currentGamePackage.value = currentPackage.copy(state = GameState.COMPLETED)
        }
        cleanup()
    }

    fun cleanup() {
        jsEngine.cleanup()
        _currentGamePackage.value = null
        _uiElements.value = emptyList()

        // Clear saved game state when cleaning up
        CoroutineScope(Dispatchers.IO).launch {
            gameStorage.clearSavedGame()
        }
    }

    private suspend fun getJsValue(id: String): String? {
        return jsEngine.getJsValue(id).getOrNull()
    }

    suspend fun saveGameState(gameStateJson: String) = withContext(Dispatchers.IO) {
        gameStorage.saveGameState(gameStateJson)
    }

    /**
     * Check if there is a saved game available
     */
    suspend fun hasSavedGame(): Boolean = withContext(Dispatchers.IO) {
        return@withContext gameStorage.hasSavedGame()
    }

    /**
     * Load saved game and restore the state
     */
    suspend fun loadSavedGame() = withContext(Dispatchers.IO) {
        val savedState = gameStorage.loadGameState()
        if (savedState != null) {
            // Initialize the game first
            jsEngine.evaluateJs(demoGame) // TODO total blunder

            // Restore the saved state by evaluating JavaScript that reconstructs the variables
            jsEngine.evaluateJs("""
                const savedState = $savedState;
                Object.keys(savedState).forEach(key => {
                    if (key.startsWith('_')) {
                        globalThis[key] = savedState[key];
                    }
                });
            """.trimIndent())

            // Start with the current task from the restored state
            val currentTask = getJsValue("_currentTask") ?: "start"
            setCurrentTask(currentTask)
            startLocationMonitoring()
        }
    }
}