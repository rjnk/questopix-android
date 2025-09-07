package com.rejnek.oog.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.compose.runtime.Composable
import com.rejnek.oog.data.engine.JsGameEngine
import com.rejnek.oog.data.model.Area
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Refactored GameRepository that delegates to specialized repositories
 * Maintains the same public API for backward compatibility
 */
class GameRepository(
    context: Context
) {
    // Specialized repositories
    private val jsEngine = JsGameEngine(context)
    val gameStorageRepository = GameStorageRepository(context)
    val gameLocationRepository = GameLocationRepository(context)
    val gameUIRepository = GameUIRepository()
    val gameItemRepository = GameItemRepository()

    // Current Game Package & task
    private val _currentGamePackage = MutableStateFlow<GamePackage?>(null)
    val currentGamePackage = _currentGamePackage.asStateFlow()

    // Mutex
    private val executeOnStartMutex = Mutex()

    // Initialize method for JS engine setup
    suspend fun initialize(): Result<Unit> {
        return jsEngine.initialize(this@GameRepository).map { }
    }

    // Game Initialization
    suspend fun initializeGameFromLibrary(gameId: String) = withContext(Dispatchers.IO) {
        val game = gameStorageRepository.getGameById(gameId)
        startGame(game ?: throw IllegalArgumentException("Game with ID $gameId not found in library"))
    }

    suspend fun loadSavedGame() = withContext(Dispatchers.IO) {
        val savedGamePackage = gameStorageRepository.getSavedGamePackage()
        startGame(savedGamePackage ?: throw IllegalStateException("No saved game found"))
    }

    suspend fun startGame(gamePackage: GamePackage) {
        // Set the current game package
        _currentGamePackage.value = gamePackage

        // Initialize the game first with the game code
        jsEngine.evaluateJs(gamePackage.gameCode)

        // If there's saved game state, restore it
        gamePackage.gameState?.let { gameStateJson ->
            jsEngine.restoreState(gameStateJson)
        }

        // Start location monitoring
        val areasToMonitor = generateAreasForMonitoring(gamePackage)
        gameLocationRepository.startLocationMonitoring(areasToMonitor) { areaId -> setCurrentTask(areaId) }

        // Start with the current task from the game package, it will be "start" if new game
        setCurrentTask(gamePackage.currentTaskId)
    }

    suspend fun generateAreasForMonitoring(gamePackage: GamePackage): List<Area> {
        val areasToMonitor = ArrayList<Area>()

        for (taskId in gamePackage.getTaskIds()) {
            val area = jsEngine.getArea(taskId)
            area?.let { areasToMonitor.add(it) }
        }

        return areasToMonitor
    }

    // Game state management
    fun setCurrentGamePackage(gamePackage: GamePackage) {
        _currentGamePackage.value = gamePackage
    }

    suspend fun refresh() {
        val currentTask = jsEngine.getJsValue("_currentTask").getOrNull() ?: ""
        setCurrentTask(currentTask)
    }

     fun evaluateJs(code: String) = CoroutineScope(Dispatchers.IO).launch {
        jsEngine.evaluateJs(code)
    }

    suspend fun setCurrentTask(elementId: String) {
        // if(_currentGamePackage.value?.currentTaskId == elementId) return // prevent double execution

        _currentGamePackage.value?.currentTaskId = elementId
        gameUIRepository.clearUIElements()
        executeOnStart()

        Log.d("GameRepository", "Selected element set to $elementId")
    }

    suspend fun executeOnStart() {
        executeOnStartMutex.withLock {
            val taskId = _currentGamePackage.value?.currentTaskId ?: throw IllegalStateException("Current task ID is null")
            jsEngine.executeOnStart(taskId)
        }
    }

    // UI Management
    fun addUIElement(element: @Composable () -> Unit) {
        gameUIRepository.addUIElement(element)
    }

    // Game Lifecycle
    fun finishGame() {
        _currentGamePackage.value?.let { currentPackage ->
            _currentGamePackage.value = currentPackage.copy(state = GameState.FINISHED)
        }
        _currentGamePackage.value?.let { currentPackage ->
            _currentGamePackage.value = currentPackage.copy(state = GameState.ARCHIVED)
        }
        CoroutineScope(Dispatchers.IO).launch {
            gameStorageRepository.saveGame(_currentGamePackage.value ?: throw IllegalStateException("No current game package"))
            gameStorageRepository.clearSavedGame()
        }
        cleanup()
    }

    fun cleanup() {
        jsEngine.cleanup()
        _currentGamePackage.value = null
        gameUIRepository.clearUIElements()

        // Clear saved game state when cleaning up
        CoroutineScope(Dispatchers.IO).launch {
            gameStorageRepository.clearSavedGame()
        }
    }
}