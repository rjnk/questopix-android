package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Actions/events that can be performed in this screen
    fun onLoadGameClicked() {
        // Handle loading game logic here
        // For now this is just a shell since the navigation is handled in the Router
    }
}

// Data class representing the UI state of the Home screen
data class HomeUiState(
    val isLoading: Boolean = false,
    val availableGames: List<String> = emptyList()
)
