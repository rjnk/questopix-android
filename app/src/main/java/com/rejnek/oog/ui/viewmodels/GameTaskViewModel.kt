package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameTaskViewModel : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameTaskUiState())
    val uiState: StateFlow<GameTaskUiState> = _uiState.asStateFlow()

    // Actions/events that can be performed in this screen
    fun onStart() {
        // Load task data
    }

    fun onAnswerSubmitted(answer: String) {
        // Process the submitted answer
        // This would typically validate the answer and update the UI state
    }

    fun onContinueClicked() {
        // Handle continue logic here after task completion
        // For now this is just a shell since the navigation is handled in the Router
    }
}

// Data class representing the UI state of the GameTask screen
data class GameTaskUiState(
    val isLoading: Boolean = false,
    val taskTitle: String = "Game Task",
    val taskDescription: String = "This is where the player completes a task or answers a question to progress in the game.",
    val isTaskCompleted: Boolean = false,
    val feedback: String = ""
)
