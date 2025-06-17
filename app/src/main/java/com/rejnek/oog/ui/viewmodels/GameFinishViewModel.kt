package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameFinishViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameFinishUiState())
    val uiState: StateFlow<GameFinishUiState> = _uiState.asStateFlow()

    init {
        loadGameCompletionData()
    }

    // Actions/events that can be performed in this screen
    fun onStart() {
        loadGameCompletionData()
    }

    private fun loadGameCompletionData() {
        viewModelScope.launch {

        }
    }

    fun onBackToHomeClicked() {
        // Clean up game data when returning to home
        gameRepository.cleanup()
    }
}

// Data class representing the UI state of the GameFinish screen
data class GameFinishUiState(
    val isLoading: Boolean = false,
    val completionMessage: String = "Congratulations! You have completed the game.",
    val score: Int = 0,
    val timeTaken: String = "",
    val achievements: List<String> = emptyList(),
    val completedElements: List<String> = emptyList(),
    val visitedLocations: List<String> = emptyList()
)
