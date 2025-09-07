package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            gamePackages.value = gameRepository.gameStorageRepository.getLibraryGames()
            _gameIsInProgress.value = gamePackages.value.any {
                it.state == GameState.IN_PROGRESS
            }
        }
    }

    // Importing games
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
                gameRepository.gameStorageRepository.addGameToLibrary(gamePackage)
                onOpenGameInfo(gamePackage.getId())
                loadLibraryGames()
            }
        }
    }

    fun onConfirmDuplicateReplace(
        onOpenGameInfo: (String) -> Unit
    ) {
        viewModelScope.launch {
            _pendingGamePackage.value?.let { gamePackage ->
                gameRepository.gameStorageRepository.addGameToLibrary(gamePackage)
                onOpenGameInfo(gamePackage.getId())
                loadLibraryGames()
            }
            _showDuplicateDialog.value = false
        }
    }

    fun onCancelDuplicateReplace() {
        _showDuplicateDialog.value = false
    }

    // Selection mode and deleting
    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            _selectedGameIds.value = emptySet()
        }
    }

    fun toggleGameSelection(gameId: String) {
        val currentSelected = _selectedGameIds.value.toMutableSet()
        if (currentSelected.contains(gameId)) {
            currentSelected.remove(gameId)
        } else {
            currentSelected.add(gameId)
        }
        _selectedGameIds.value = currentSelected
    }

    fun deleteSelectedGames() {
        viewModelScope.launch {
            _selectedGameIds.value.forEach { gameId ->
                gameRepository.gameStorageRepository.removeGameFromLibrary(gameId)
            }
            _selectedGameIds.value = emptySet()
            _isSelectionMode.value = false
            loadLibraryGames()
        }
    }

    fun selectAllGames() {
        _selectedGameIds.value = gamePackages.value.map { it.getId() }.toSet()
    }
}
