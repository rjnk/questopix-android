package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description = _description.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // Expose UI elements from the repository
    val uiElements = gameRepository.uiElements


    init {
        // Observe current element changes in a separate coroutine
        viewModelScope.launch {
            gameRepository.currentElement.collect { elem ->
                if(elem.elementType == GameElementType.FINISH) {
                    _navigationEvents.emit(NavigationEvent.Finish)
                    return@collect
                }
                else{
                    _name.value = elem.name
                    _description.value = elem.description
                }
            }
        }
    }

    // In GameNavigationTextViewModel
    sealed class NavigationEvent {
        object Finish : NavigationEvent()
    }
}
