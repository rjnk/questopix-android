package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameStartViewModel : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameStartUiState())
    val uiState: StateFlow<GameStartUiState> = _uiState.asStateFlow()

    // Actions/events that can be performed in this screen
    fun onStart() {
        // Initialize game data, load initial instructions, etc.
    }

    fun onContinueClicked() {
        // Handle continue logic here
        // For now this is just a shell since the navigation is handled in the Router
    }
}

// Data class representing the UI state of the GameStart screen
data class GameStartUiState(
    val isLoading: Boolean = false,
    val gameTitle: String = "",
    val gameDescription: String = "",
    val introductionText: String = "Welcome to the game! Here you'll see game introduction and initial instructions."
)
