package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.launch

class SecondaryTabViewModel(
    val gameRepository: GameRepository
) : ViewModel() {
    val uiElements = gameRepository.uiElements

    init {
        viewModelScope.launch {
            gameRepository.secondaryTabElementId.collect { elementId ->
                if (elementId.isNotEmpty()) {
                    gameRepository.setCurrentElement(elementId)
                }
            }
        }
    }
}