/*
 * Created with Github Copilot
 */
package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the game library screen.
 *
 * Manages game list display, selection mode for batch operations,
 * game import from ZIP files, and duplicate game handling.
 *
 * @param gameRepository Repository for game storage operations
 */
class LibraryViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val gamePackages = MutableStateFlow<List<GamePackage>>(emptyList())
    val libraryGames = gamePackages.asStateFlow()

    // Selection mode
    private val _selectedGameIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedGameIds = _selectedGameIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    // Duplicate handling
    private val _showDuplicateDialog = MutableStateFlow(false)
    val showDuplicateDialog = _showDuplicateDialog.asStateFlow()

    private val _pendingGamePackage = MutableStateFlow<GamePackage?>(null)
    val pendingGamePackage = _pendingGamePackage.asStateFlow()

    private val _gameIsInProgress = MutableStateFlow(false)
    val gameIsInProgress = _gameIsInProgress.asStateFlow()

    init {
        loadLibraryGames()
    }

    private fun loadLibraryGames() {
        viewModelScope.launch {
            gamePackages.value = gameRepository.storageRepository.getLibraryGames()
            _gameIsInProgress.value = gamePackages.value.any {
                it.state == GameState.IN_PROGRESS
            }
        }
    }

    /** Imports a game package, checking for duplicates first. */
    fun onAddGameFromFile(
        gamePackage: GamePackage,
        onOpenGameInfo: (String) -> Unit
    ) {
        viewModelScope.launch {
            // Check if game with this ID already exists
            val existingGame = gamePackages.value.find { it.getId() == gamePackage.getId() }
            if (existingGame != null) {
                // Show duplicate confirmation dialog
                _pendingGamePackage.value = gamePackage
                _showDuplicateDialog.value = true
            } else {
                // Add directly to library
                gameRepository.storageRepository.addGameToLibrary(gamePackage)
                onOpenGameInfo(gamePackage.getId())
                loadLibraryGames()
            }
        }
    }

    /** Confirms replacing an existing game with the imported one. */
    fun onConfirmDuplicateReplace(
        onOpenGameInfo: (String) -> Unit
    ) {
        viewModelScope.launch {
            _pendingGamePackage.value?.let { gamePackage ->
                gameRepository.storageRepository.addGameToLibrary(gamePackage)
                onOpenGameInfo(gamePackage.getId())
                loadLibraryGames()
            }
            _showDuplicateDialog.value = false
        }
    }

    /** Cancels the duplicate import operation. */
    fun onCancelDuplicateReplace() {
        _showDuplicateDialog.value = false
    }

    /** Toggles selection mode on/off, clearing selection when exiting. */
    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            _selectedGameIds.value = emptySet()
        }
    }

    /** Toggles selection state of a specific game. */
    fun toggleGameSelection(gameId: String) {
        val currentSelected = _selectedGameIds.value.toMutableSet()
        if (currentSelected.contains(gameId)) {
            currentSelected.remove(gameId)
        } else {
            currentSelected.add(gameId)
        }
        _selectedGameIds.value = currentSelected
    }

    /** Deletes all selected games and exits selection mode. */
    fun deleteSelectedGames() {
        viewModelScope.launch {
            _selectedGameIds.value.forEach { gameId ->
                gameRepository.storageRepository.removeGameFromLibrary(gameId)
            }
            _selectedGameIds.value = emptySet()
            _isSelectionMode.value = false
            loadLibraryGames()
        }
    }

    /** Selects all games in the library. */
    fun selectAllGames() {
        _selectedGameIds.value = gamePackages.value.map { it.getId() }.toSet()
    }
}
