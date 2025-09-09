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
import com.rejnek.oog.ui.components.library.loadBundledGames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Refactored GameRepository that delegates to specialized repositories
 * Maintains the same public API for backward compatibility
 */
class GameRepository(
    private val context: Context
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
    private val canSetElement = AtomicBoolean(true)

    // Initialize method for JS engine setup
    suspend fun initialize(): Result<Unit> {
        val res = jsEngine.initialize(this@GameRepository).map { }
        Log.d("GameRepository", "JS engine initialized successfully")
        return res
    }

    // Game Initialization
    suspend fun initializeGameFromLibrary(gameId: String) = withContext(Dispatchers.IO) {
        val game = gameStorageRepository.getGameById(gameId)
        startGame(game ?: throw GameRepositoryException("Game with ID $gameId not found in library"))
    }

    suspend fun loadSavedGame() = withContext(Dispatchers.IO) {
        val savedGamePackage = gameStorageRepository.getSavedGamePackage()
        startGame(savedGamePackage ?: throw GameRepositoryException("No saved game found"))
    }

    suspend fun preloadGames() = withContext(Dispatchers.IO) {
        val games = loadBundledGames(context)

        for (game in games) {
            val existingGame = gameStorageRepository.getGameById(game.getId())

            if (existingGame == null) {
                gameStorageRepository.addGameToLibrary(game)
                Log.d("GameRepository", "Preloaded bundled game: ${game.getId()}")
            } else {
                Log.d("GameRepository", "Bundled game already exists, skipping: ${game.getId()}")
            }
        }
    }

    suspend fun startGame(gamePackage: GamePackage) {
        if(jsEngine.isInitialized.not()) {
            throw GameRepositoryException("JS Engine not initialized")
        }

        // Set the current game package
        _currentGamePackage.value = gamePackage

        // Initialize the game first with the game code
        jsEngine.evaluateJs(gamePackage.gameCode)

        // If there's saved game state, restore it
        gamePackage.gameState?.let { gameStateJson ->
            jsEngine.restoreState(gameStateJson)
        }

        // Start with the current task from the game package, it will be "start" if new game
        setCurrentTask(gamePackage.currentTaskId)

        // Start location monitoring
        startLocationMonitoring()
    }

    suspend fun startLocationMonitoring() {
        val gamePackage = _currentGamePackage.value ?: return
        if (gamePackage.state == GameState.ARCHIVED) return

        val areasToMonitor = generateAreasForMonitoring(gamePackage)
        gameLocationRepository.startMonitoringAreas(areasToMonitor) { areaId ->
            if(jsEngine.getJsValue("isEnabled(\"$areaId\")").getOrNull() == "true") {
                setCurrentTask(areaId)
            }
        }
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
        if(! canSetElement.get()) return
        canSetElement.set(false)

        _currentGamePackage.value?.currentTaskId = elementId
        gameUIRepository.clearUIElements()
        jsEngine.executeOnStart(elementId)

        Log.d("GameRepository", "Selected element set to $elementId")
        canSetElement.set(true)
    }

    // UI Management
    fun addUIElement(element: @Composable () -> Unit) {
        gameUIRepository.addUIElement(element)
    }

    fun removeLastUIElement() {
        gameUIRepository.removeLastUIElement()
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
            gameStorageRepository.saveGame(_currentGamePackage.value ?: throw GameRepositoryException("No current game package"))
            cleanup()
        }
    }

    fun pauseCurrentGame() {
        CoroutineScope(Dispatchers.IO).launch {
            gameStorageRepository.saveGame(_currentGamePackage.value ?: throw IllegalStateException("No current game package"))
            cleanup()
        }
    }

    fun quitCurrentGame() {
        val gamePackage = _currentGamePackage.value ?: return

        // Save the game state with reseted values
        val clearPackage = GamePackage(
            gameInfo = gamePackage.gameInfo,
            gameCode = gamePackage.gameCode,
            state = GameState.NOT_STARTED,
            importedAt = gamePackage.importedAt,
            currentTaskId = "start",
            gameState = null // Clear the saved state
        )
        CoroutineScope(Dispatchers.IO).launch {
            gameStorageRepository.saveGame(clearPackage)
        }
        gameStorageRepository.deleteAllImages(clearPackage.getId())
        cleanup()
    }

    fun cleanup() {
        CoroutineScope(Dispatchers.Main).launch {
            _currentGamePackage.value = null
            gameUIRepository.clearUIElements()
            gameLocationRepository.stopLocationMonitoring()

            // Re-initialize JS engine for next game
            jsEngine.cleanup()
            jsEngine.initialize(this@GameRepository)
        }
    }
}

class GameRepositoryException(message: String) : Exception(message)