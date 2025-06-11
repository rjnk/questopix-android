package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.util.Log
import com.rejnek.oog.data.model.GameElementType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameNavigationTextViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description = _description.asStateFlow()

    private val _currentElementType = MutableStateFlow<GameElementType>(GameElementType.UNKNOWN)
    val currentElementType = _currentElementType.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        refresh()

        viewModelScope.launch {
//            gameRepository.currentGame.collectLatest { updatedGame ->
//                updatedGame?.let { game ->
//                    Log.d("GameViewModel", "Game updated: ${game.currentElement.name}, index: ${game.currentElementIndex}")
//                    _currentElementType.value = game.currentElement.elementType
//                }
//            }
        }
    }

    fun refresh(){
        _name.value = gameRepository.currentGame.value?.currentElement?.name ?: "Loading..."
        _description.value = gameRepository.currentGame.value?.currentElement?.description ?: "Loading..."
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            // run the onContinue script for the current game element
            gameRepository.executeOnContinue(null)

            // Check if the current element is a finish element
            if(gameRepository.currentGame.value?.currentElement?.elementType == GameElementType.FINISH){
                _navigationEvents.emit(NavigationEvent.Finish)
            }
            // Update the UI
            else{
                refresh()
            }
        }
    }

    // In GameNavigationTextViewModel
    sealed class NavigationEvent {
        object Finish : NavigationEvent()
    }
}
