package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameInfoViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _gamePackage = MutableStateFlow<GamePackage?>(null)
    val gamePackage = _gamePackage.asStateFlow()

    private val _showFarAwayToast = MutableStateFlow(false)
    val showFarAwayToast = _showFarAwayToast.asStateFlow()

    fun loadGameInfo(gameId: String, onGameStarted: () -> Unit) {
        viewModelScope.launch {
            val games = gameRepository.gameStorageRepository.getLibraryGames()
            val tempPackage = games.find { it.getId() == gameId }

            if (tempPackage?.state != GameState.NOT_STARTED) {
                startGame(onGameStarted, gameId)
            } else {
                _gamePackage.value = tempPackage
            }
        }

    }

    fun startGame(onGameStarted: () -> Unit){
        val gameId = gamePackage.value?.getId()
        if (gameId != null) {
            startGame(onGameStarted, gameId)
        }
    }

    fun startGame(onGameStarted: () -> Unit, gameId: String) {
        viewModelScope.launch {
            gameRepository.initializeGameFromLibrary(gameId)
            onGameStarted()
        }
    }
}