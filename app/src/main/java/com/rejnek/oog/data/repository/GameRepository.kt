package com.rejnek.oog.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import androidx.compose.runtime.Composable
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Refactored GameRepository that delegates to specialized repositories
 * Maintains the same public API for backward compatibility
 */
class GameRepository(
    context: Context
) {
    // Specialized repositories
    val gameEngineRepository = GameEngineRepository(context)
    val gameStorageRepository = GameStorageRepository(context)
    val gameLocationRepository = GameLocationRepository(context)
    val gameUIRepository = GameUIRepository()
    val gameItemRepository = GameItemRepository()

    // Current Game Package & task
    private val _currentGamePackage = MutableStateFlow<GamePackage?>(null)
    val currentGamePackage = _currentGamePackage.asStateFlow()

    // Initialize method for JS engine setup
    suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext gameEngineRepository.initialize(this@GameRepository)
    }

    // Game Library Operations
    suspend fun initializeGameFromLibrary(gameId: String) = withContext(Dispatchers.IO) {
        val game = gameStorageRepository.getGameById(gameId)
        if (game != null) {
            _currentGamePackage.value = game
            gameEngineRepository.initializeGame(game.gameCode)
            setCurrentTask("start")
            startLocationMonitoring()
        }
    }

    fun getLibraryGames() = gameStorageRepository.getLibraryGames()

    // Game state management
    fun updateCurrentGamePackage(gamePackage: GamePackage) {
        _currentGamePackage.value = gamePackage
    }

    // Location and Game State Management
    private fun startLocationMonitoring() {
        gameLocationRepository.startLocationMonitoring {
            executeOnEnter()
        }
    }

    suspend fun refresh() {
        val currentTask = gameEngineRepository.getJsValue("_currentTask") ?: ""
        setCurrentTask(currentTask)
    }

    suspend fun setCurrentTask(elementId: String) {
        _currentGamePackage.value?.currentTaskId = elementId
        gameUIRepository.clearUIElements()

        Log.d("GameRepository", "Selected element set to $elementId")

        if (gameLocationRepository.checkLocation()) executeOnEnter()
        else executeOnStart()
    }

    suspend fun executeOnStart() {
        val taskId = _currentGamePackage.value?.currentTaskId ?: throw IllegalStateException("Current task ID is null")
        gameEngineRepository.executeOnStart(taskId)
    }

    suspend fun executeOnEnter() {
        val taskId = _currentGamePackage.value?.currentTaskId ?: throw IllegalStateException("Current task ID is null")
        gameEngineRepository.executeOnEnter(taskId)
    }

    // UI Management
    fun addUIElement(element: @Composable () -> Unit) {
        gameUIRepository.addUIElement(element)
    }

    // Game Lifecycle
    fun finishGame() {
        _currentGamePackage.value?.let { currentPackage ->
            _currentGamePackage.value = currentPackage.copy(state = GameState.COMPLETED)
        }
        CoroutineScope(Dispatchers.IO).launch {
            gameStorageRepository.saveGame(_currentGamePackage.value ?: throw IllegalStateException("No current game package"))
            gameStorageRepository.clearSavedGame()
        }
        cleanup()
    }

    fun cleanup() {
        gameEngineRepository.cleanup()
        _currentGamePackage.value = null
        gameUIRepository.clearUIElements()

        // Clear saved game state when cleaning up
        CoroutineScope(Dispatchers.IO).launch {
            gameStorageRepository.clearSavedGame()
        }
    }

    suspend fun loadSavedGame() = withContext(Dispatchers.IO) {
        val savedGamePackage = gameStorageRepository.getSavedGamePackage()

        if (savedGamePackage != null) {
            // Set the current game package
            _currentGamePackage.value = savedGamePackage

            // Initialize the game first with the game code
            gameEngineRepository.evaluateJs(savedGamePackage.gameCode)

            // If there's saved game state, restore it
            savedGamePackage.gameState?.let { gameStateJson ->
                gameEngineRepository.evaluateJs("""
                    const savedState = $gameStateJson;
                    Object.keys(savedState).forEach(key => {
                        if (key.startsWith('_')) {
                            globalThis[key] = savedState[key];
                        }
                    });
                """.trimIndent())
            }

            // Start with the current task from the saved package
            setCurrentTask(savedGamePackage.currentTaskId)
            startLocationMonitoring()
        }
    }
}