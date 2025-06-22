package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.launch

class GameMenuViewModel(
    val gameRepository: GameRepository
) : ViewModel() {

    val visibleElements = gameRepository.visibleElements

    fun clickOnElement(elementId: String) {
        viewModelScope.launch {
            gameRepository.setCurrentElement(elementId)
        }
    }
}