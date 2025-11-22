package com.rejnek.oog.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.compose.runtime.Composable
import com.rejnek.oog.engine.JsGameEngine
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
 * Repository managing game state, initialization, and interactions
 * Delegates commands to specialized repositories (storage, location, UI, commands)
 *
 * @param context Application context for resource access
 */
class GameRepository(
    private val context: Context
) {
    // JS Game Engine instance
    private val jsEngine = JsGameEngine(context)

    // Specialized repositories
    val storageRepository = StorageRepository(context)
    val locationRepository = LocationRepository(context)
    val gameUIRepository = GameUIRepository()
    val commandRepository = CommandRepository()

    // Current Game Package & task
    private val _currentGamePackage = MutableStateFlow<GamePackage?>(null)
    val currentGamePackage = _currentGamePackage.asStateFlow()

    // Mutex for setting current task
    private val canSetElement = AtomicBoolean(true)

    /**
     * Initializes the JS game engine.
     * Must be called before starting any game.
     * @return Result indicating success or failure
     */
    suspend fun initialize(): Result<Unit> {
        val res = jsEngine.initialize(this@GameRepository).map { }
        Log.d("GameRepository", "JS engine initialized successfully")
        return res
    }

    /**
     * Initializes and starts a new game from the library by game ID.
     * @param gameId ID of the game to start
     * @throws GameRepositoryException if the game is not found in the library
     * @return Unit
     */
    suspend fun initializeGameFromLibrary(gameId: String) = withContext(Dispatchers.IO) {
        val game = storageRepository.getGameById(gameId)
        startGame(game ?: throw GameRepositoryException("Game with ID $gameId not found in library"))
    }

    /**
     * Loads the saved in-progress game from storage and starts it.
     * As the game can be only one at a time, we don't pass gameId
     * @throws GameRepositoryException if no saved game is found
     * @return Unit
     */
    suspend fun loadSavedGame() = withContext(Dispatchers.IO) {
        val savedGamePackage = storageRepository.getSavedGamePackage()
        startGame(savedGamePackage ?: throw GameRepositoryException("No saved game found"))
    }

    /**
     * Preloads bundled games into the library if they don't already exist.
     * This is typically called on first app launch to populate the library.
     */
    suspend fun preloadGames() = withContext(Dispatchers.IO) {
        val games = loadBundledGames(context)

        for (game in games) {
            val existingGame = storageRepository.getGameById(game.getId())

            if (existingGame == null) {
                storageRepository.addGameToLibrary(game)
                Log.d("GameRepository", "Preloaded bundled game: ${game.getId()}")
            } else {
                Log.d("GameRepository", "Bundled game already exists, skipping: ${game.getId()}")
            }
        }
    }

    /**
     * Starts a game with the provided GamePackage.
     * Initializes the JS engine, restores state if available, and sets the current task.
     *
     * @param gamePackage GamePackage object containing game data
     * @throws GameRepositoryException if the JS engine is not initialized
     * @return Unit
     */
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

    /**
     * Starts monitoring location areas relevant to the current game.
     */
    suspend fun startLocationMonitoring() {
        val gamePackage = _currentGamePackage.value ?: return
        if (gamePackage.state == GameState.ARCHIVED) return

        val areasToMonitor = generateAreasForMonitoring(gamePackage)
        locationRepository.startMonitoringAreas(areasToMonitor) { areaId ->
            if(jsEngine.getJsValue("isEnabled(\"$areaId\")").getOrNull() == "true") {
                setCurrentTask(areaId)
            }
        }
    }

    /**
     * Generates a list of areas to monitor based on the tasks defined in the game package.
     * @param gamePackage GamePackage object containing game data
     * @return List of Area objects to monitor
     */
    suspend fun generateAreasForMonitoring(gamePackage: GamePackage): List<Area> {
        val areasToMonitor = ArrayList<Area>()

        for (taskId in gamePackage.getTaskIds()) {
            val area = jsEngine.getArea(taskId)
            area?.let { areasToMonitor.add(it) }
        }

        return areasToMonitor
    }

    // ========== GAME STATE MANAGEMENT ==========

    /** Sets the current game package.
     * @param gamePackage GamePackage object to set as current
     */
    fun setCurrentGamePackage(gamePackage: GamePackage) {
        _currentGamePackage.value = gamePackage
    }

    /**
     * Refreshes the current task from the JS engine.
     * This redraws the UI for the current task.
     */
    suspend fun refresh() {
        val currentTask = jsEngine.getJsValue("_currentTask").getOrNull() ?: ""
        setCurrentTask(currentTask)
    }

    /**
     * Evaluates arbitrary JS code in the game engine.
     * @param code JavaScript code string to evaluate
     */
     fun evaluateJs(code: String) = CoroutineScope(Dispatchers.IO).launch {
        jsEngine.evaluateJs(code)
    }

    /**
     * Sets the current task by its element ID.
     * Updates the JavaScript engine, sets a new game package and redraws the UI.
     * Uses a mutex to prevent concurrent updates.
     *
     * @param elementId ID of the task element to set as current
     */
    suspend fun setCurrentTask(elementId: String) {
        if(! canSetElement.get()) return
        canSetElement.set(false)

        _currentGamePackage.value?.currentTaskId = elementId
        gameUIRepository.clearUIElements()
        jsEngine.executeOnStart(elementId)

        Log.d("GameRepository", "Selected element set to $elementId")
        canSetElement.set(true)
    }

    // ========== UI MANAGEMENT ==========

    /**
     * Adds a UI element (Composable function) to be displayed in the GameTaskScreen
     * @param element Composable function representing the UI element
     */
    fun addUIElement(element: @Composable () -> Unit) {
        gameUIRepository.addUIElement(element)
    }

    // ======== GAME LIFECYCLE MANAGEMENT ==========

    /**
     * Marks the current game first as finished, then archived.
     * Saves the final game state to storage and cleans up resources.
     */
    fun finishGame() {
        _currentGamePackage.value?.let { currentPackage ->
            _currentGamePackage.value = currentPackage.copy(state = GameState.FINISHED)
        }
        _currentGamePackage.value?.let { currentPackage ->
            _currentGamePackage.value = currentPackage.copy(state = GameState.ARCHIVED)
        }
        CoroutineScope(Dispatchers.IO).launch {
            storageRepository.saveGame(_currentGamePackage.value ?: throw GameRepositoryException("No current game package"))
            cleanup()
        }
    }

    /**
     * Pauses the current game by saving its state to storage.
     * Cleans up resources without altering the game state.
     */
    fun pauseCurrentGame() {
        CoroutineScope(Dispatchers.IO).launch {
            storageRepository.saveGame(_currentGamePackage.value ?: throw IllegalStateException("No current game package"))
            cleanup()
        }
    }

    /**
     * Resets and quits the current game.
     * Clears saved state and all associated images from storage.
     */
    fun resetCurrentGame() {
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
            storageRepository.saveGame(clearPackage)
        }
        storageRepository.deleteAllImages(clearPackage.getId())
        cleanup()
    }

    /**
     * Cleans up resources after game ends, pauses, or resets.
     * Clears current game package, UI elements, stops location monitoring,
     * and re-initializes the JS engine for the next game.
     */
    fun cleanup() {
        CoroutineScope(Dispatchers.Main).launch {
            _currentGamePackage.value = null
            gameUIRepository.clearUIElements()
            locationRepository.stopLocationMonitoring()

            // Re-initialize JS engine for next game
            jsEngine.cleanup()
            jsEngine.initialize(this@GameRepository)
        }
    }
}
