package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // UI state
    private val _uiState = MutableStateFlow(GameTaskUiState())
    val uiState: StateFlow<GameTaskUiState> = _uiState.asStateFlow()

    init {
        loadTaskData()
    }

    // Actions/events that can be performed in this screen
    fun onStart() {
        loadTaskData()
    }

    private fun loadTaskData() {
//        viewModelScope.launch {
//            val context = gameRepository.currentGame.value
//            if (context != null) {
//                // Find task elements that are visible
//                val taskElements = gameRepository.getElementsOfType(GameElementType.TASK)
//                    // .filter { gameRepository.isElementVisible(it.name) }
//
//                if (taskElements.isNotEmpty()) {
//                    val currentTask = taskElements.first()
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        taskTitle = currentTask.name,
//                        taskDescription = currentTask.description,
//                        isTaskCompleted = gameRepository.isElementCompleted(currentTask.name),
//                        coordinates = currentTask.coordinates
//                    )
//                } else {
//                    // Show generic task if no specific task is visible
//                    _uiState.value = _uiState.value.copy(
//                        isLoading = false,
//                        taskTitle = "Game Task",
//                        taskDescription = "This is where the player completes a task or answers a question to progress in the game.",
//                        isTaskCompleted = false
//                    )
//                }
//            }
//        }
    }

    fun onContinueClicked() {
//        viewModelScope.launch {
//
//
//
//            // Find current task element and execute its onContinue
//            val taskElements = gameRepository.getElementsOfType(GameElementType.TASK)
//                .filter { gameRepository.isElementVisible(it.name) }
//
//            if (taskElements.isNotEmpty()) {
//                val currentTask = taskElements.first()
//                gameRepository.executeOnContinue(currentTask)
//            }
//        }
//    }
    }
}

// Data class representing the UI state of the GameTask screen
data class GameTaskUiState(
    val isLoading: Boolean = false,
    val taskTitle: String = "Game Task",
    val taskDescription: String = "This is where the player completes a task or answers a question to progress in the game.",
    val isTaskCompleted: Boolean = false,
    val feedback: String = "",
    val coordinates: com.rejnek.oog.data.model.Coordinates? = null
)
