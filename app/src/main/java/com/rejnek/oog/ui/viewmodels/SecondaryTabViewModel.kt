package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel.NavigationEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SecondaryTabViewModel(
    val gameRepository: GameRepository
) : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val uiElements = gameRepository.uiElements

    init {
        viewModelScope.launch {
            gameRepository.currentElement.collect { elem ->
                if(elem?.elementType == GameElementType.FINISH) {
                    _navigationEvents.emit(NavigationEvent.Finish)
                    return@collect
                }
            }
        }
    }
}