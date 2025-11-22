package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen.
 *
 * Checks for saved game state and initializes the game repository.
 * Handles loading previously saved games.
 *
 * @param gameRepository Repository for game state and initialization
 */
class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _hasSavedGame = MutableStateFlow(false)
    val hasSavedGame = _hasSavedGame.asStateFlow()

    init {
        viewModelScope.launch {
            _hasSavedGame.value = gameRepository.storageRepository.hasSavedGame()
            gameRepository.initialize()
        }
    }

    /** Loads a previously saved game from storage. */
    fun onLoadSavedClicked() {
        viewModelScope.launch {
            gameRepository.loadSavedGame()
        }
    }
}
