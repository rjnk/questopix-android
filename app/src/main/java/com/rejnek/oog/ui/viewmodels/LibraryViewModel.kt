package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val gamesPackage = MutableStateFlow<List<GamePackage>>(emptyList())
    val libraryGames = gamesPackage.asStateFlow()

    private val _selectedGameIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedGameIds = _selectedGameIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    private val _showDuplicateDialog = MutableStateFlow(false)
    val showDuplicateDialog = _showDuplicateDialog.asStateFlow()

    private val _pendingGamePackage = MutableStateFlow<GamePackage?>(null)
    val pendingGamePackage = _pendingGamePackage.asStateFlow()

    init {
        loadLibraryGames()
    }

    private fun loadLibraryGames() {
        viewModelScope.launch {
            gamesPackage.value = gameRepository.getLibraryGames()
        }
    }

    fun onAddGameFromFile(gamePackage: GamePackage) {
        viewModelScope.launch {
            // Check if game with this ID already exists
            val existingGame = gamesPackage.value.find { it.getId() == gamePackage.getId() }
            if (existingGame != null) {
                // Show duplicate confirmation dialog
                _pendingGamePackage.value = gamePackage
                _showDuplicateDialog.value = true
            } else {
                // Add directly to library
                gameRepository.gameStorageRepository.addGameToLibrary(gamePackage)
                loadLibraryGames()
            }
        }
    }

    fun onConfirmDuplicateReplace() {
        viewModelScope.launch {
            _pendingGamePackage.value?.let { gamePackage ->
                // Remove existing game and add new one
                gameRepository.gameStorageRepository.removeGameFromLibrary(gamePackage.getId())
                gameRepository.gameStorageRepository.addGameToLibrary(gamePackage)
                loadLibraryGames()
            }
            _showDuplicateDialog.value = false
            _pendingGamePackage.value = null
        }
    }

    fun onCancelDuplicateReplace() {
        _showDuplicateDialog.value = false
        _pendingGamePackage.value = null
    }

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
        _selectedGameIds.value = gamesPackage.value.map { it.getId() }.toSet()
    }

    fun clearSelection() {
        _selectedGameIds.value = emptySet()
    }

    fun refreshLibrary() {
        loadLibraryGames()
    }
}
