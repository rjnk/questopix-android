package com.rejnek.oog.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun onLoadAssetGameClicked(context: Context) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            val result = gameRepository.loadGameFromAsset(context, "demo_game.js")
            
            result.fold(
                onSuccess = { game ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        gameLoadResult = "Failed to load asset game: ${error.message}"
                    )
                }
            )
        }
    }
}

// Data class representing the UI state of the Home screen
data class HomeUiState(
    val isLoading: Boolean = false,
    val availableGames: List<String> = emptyList(),
    val gameLoadResult: String = ""
)
