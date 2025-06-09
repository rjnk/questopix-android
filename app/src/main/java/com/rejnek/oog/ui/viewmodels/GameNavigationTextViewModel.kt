package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameNavigationTextViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameNavigationTextUiState())
    val uiState: StateFlow<GameNavigationTextUiState> = _uiState.asStateFlow()

    init {
        loadNavigationData()
    }

    // Actions/events that can be performed in this screen
    fun onStart() {
        loadNavigationData()
    }

    private fun loadNavigationData() {
        viewModelScope.launch {
            val context = gameRepository.currentGame.value
            if (context != null) {
                val currentNav = context.currentElement

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigationTitle = currentNav.name,
                    navigationText = currentNav.description,
                    nextLocationName = currentNav.name,
                    coordinates = currentNav.coordinates
                )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        navigationTitle = "Error",
                        navigationText = "Error loading.",
                        nextLocationName = ""
                    )
                }
            }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            gameRepository.executeOnContinue(
                gameRepository.currentGame.value?.currentElement ?: return@launch
            )
        }
    }
}

// Data class representing the UI state of the GameNavigationText screen
data class GameNavigationTextUiState(
    val isLoading: Boolean = false,
    val navigationTitle: String = "Navigation Instructions",
    val navigationText: String = "Here you can see navigation text instructions that will guide you to the next location.",
    val nextLocationName: String = "",
    val coordinates: com.rejnek.oog.data.model.Coordinates? = null
)
