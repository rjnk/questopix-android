package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameFinishViewModel : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameFinishUiState())
    val uiState: StateFlow<GameFinishUiState> = _uiState.asStateFlow()

    // Actions/events that can be performed in this screen
    fun onStart() {
        // Load game completion data, calculate final score, etc.
    }

    fun onBackToHomeClicked() {
        // Handle return to home logic here
        // For now this is just a shell since the navigation is handled in the Router
    }
}

// Data class representing the UI state of the GameFinish screen
data class GameFinishUiState(
    val isLoading: Boolean = false,
    val completionMessage: String = "Congratulations! You have completed the game.",
    val score: Int = 0,
    val timeTaken: String = "",
    val achievements: List<String> = emptyList()
)
