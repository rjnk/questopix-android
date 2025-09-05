package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // Expose UI elements from the repository
    val uiElements = gameRepository.uiElements


    init {
        // Observe current element changes in a separate coroutine
        viewModelScope.launch {
            gameRepository.selectedElement.collect { elem ->
                if (elem == null) {
                    _navigationEvents.emit(NavigationEvent.Menu)
                }
            }
        }

        viewModelScope.launch {
            gameRepository.currentGamePackage.collect { pack ->
                if (pack?.state == GameState.COMPLETED) {
                    _navigationEvents.emit(NavigationEvent.Finish)
                }
            }
        }
    }

    sealed class NavigationEvent {
        object Menu: NavigationEvent()
        object Finish : NavigationEvent()
    }
}


