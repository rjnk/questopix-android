package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameNavigationTextViewModel : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameNavigationTextUiState())
    val uiState: StateFlow<GameNavigationTextUiState> = _uiState.asStateFlow()

    // Actions/events that can be performed in this screen
    fun onStart() {
        // Load navigation instructions data
    }

    fun onContinueClicked() {
        // Handle continue logic here
        // For now this is just a shell since the navigation is handled in the Router
    }
}

// Data class representing the UI state of the GameNavigationText screen
data class GameNavigationTextUiState(
    val isLoading: Boolean = false,
    val navigationTitle: String = "Navigation Instructions",
    val navigationText: String = "Here you can see navigation text instructions that will guide you to the next location.",
    val nextLocationName: String = ""
)
