package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameInfoViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gamePackage = MutableStateFlow<GamePackage?>(null)
    val gamePackage = _gamePackage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadGameInfo(gameId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val games = gameRepository.gameStorageRepository.getLibraryGames()
                _gamePackage.value = games.find { it.getId() == gameId }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startGame(onGameStarted: () -> Unit) {
        val gameId = gamePackage.value?.getId()
        if (gameId != null) {
            viewModelScope.launch {
                gameRepository.initializeGameFromLibrary(gameId)
                onGameStarted()
            }
        }
    }
}