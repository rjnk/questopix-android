package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameStartViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameStartUiState())
    val uiState: StateFlow<GameStartUiState> = _uiState.asStateFlow()

    init {
        loadGameData()
    }

    // Actions/events that can be performed in this screen
    fun onStart() {
        loadGameData()
    }

    private fun loadGameData() {
        viewModelScope.launch {
            val currentGame = gameRepository.currentGame.value
            if (currentGame != null) {
                val startElement = currentGame.currentElement
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    gameTitle = startElement.name,
                    gameDescription = startElement.description,
                    introductionText = startElement.description.ifEmpty { 
                        "Welcome to ${startElement.name}! This is your starting point." 
                    },
                    coordinates = startElement.coordinates
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    gameTitle = "No Game Loaded",
                    gameDescription = "Please go back and load a game first.",
                    introductionText = "No game data available. Please return to the home screen and load a game."
                )
            }
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            gameRepository.currentGame.value?.let { game ->
                gameRepository.executeOnContinue(game.currentElement)
            }
        }
    }
}

// Data class representing the UI state of the GameStart screen
data class GameStartUiState(
    val isLoading: Boolean = false,
    val gameTitle: String = "",
    val gameDescription: String = "",
    val introductionText: String = "Welcome to the game! Here you'll see game introduction and initial instructions.",
    val coordinates: com.rejnek.oog.data.model.Coordinates? = null
)
