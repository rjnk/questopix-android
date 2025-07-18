package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.data.model.GameType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val _gameType = MutableStateFlow<GameType>(GameType.UNKNOWN)
    val gameType = _gameType.asStateFlow()

    // Expose UI elements from the repository
    val uiElements = gameRepository.uiElements


    init {
        // Observe current element changes in a separate coroutine
        viewModelScope.launch {
            gameRepository.currentElement.collect { elem ->
                if(elem == null){
                    _navigationEvents.emit(NavigationEvent.Menu)
                    return@collect
                }

                if(elem.elementType == GameElementType.FINISH) {
                    _navigationEvents.emit(NavigationEvent.Finish)
                    return@collect
                }
            }
        }

        // Observe game type changes
        viewModelScope.launch {
            gameRepository.gameType.collect { type ->
                _gameType.value = type
            }
        }
    }

    sealed class NavigationEvent {
        object Menu: NavigationEvent()
        object Finish : NavigationEvent()
    }
}


