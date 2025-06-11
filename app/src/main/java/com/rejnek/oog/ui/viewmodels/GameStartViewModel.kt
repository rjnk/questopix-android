package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.data.model.GameElementType
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
            val startElement = currentGame?.elements?.find { it.elementType == GameElementType.START }

            if (currentGame == null || startElement == null) {
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                gameTitle = startElement.name,
                gameDescription = startElement.description,
                introductionText = startElement.description.ifEmpty {
                    "Welcome to ${startElement.name}! This is your starting point."
                },
                coordinates = startElement.coordinates
            )
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
//            gameRepository.currentGame.value?.let { game ->
//                gameRepository.executeOnContinue(null)
//            }
        }
    }
}

// Data class representing the UI state of the GameStart screen
data class GameStartUiState(
    val isLoading: Boolean = false,
    val gameTitle: String = "Error",
    val gameDescription: String = "No game loaded.",
    val introductionText: String = "There was a problem :/",
    val coordinates: Coordinates? = null
)
