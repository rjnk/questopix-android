package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GameElement
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameMenuViewModel(
    val gameRepository: GameRepository
) : ViewModel() {

    private val _visibleElements = MutableStateFlow<List<GameElement>>(emptyList())
    val visibleElements: StateFlow<List<GameElement>> = _visibleElements.asStateFlow()

    init {
        viewModelScope.launch {
            val elements = gameRepository.getVisibleElements()
            _visibleElements.value = elements
            Log.d("GameMenuViewModel", "Visible elements: $elements")
        }
    }

    fun clickOnElement(elementId: String) {
        viewModelScope.launch {
            gameRepository.setCurrentElement(elementId)
        }
    }

    fun onSecondaryClicked() {
        viewModelScope.launch {
            if(gameRepository.getSecondaryTabElementId().isNotEmpty()){
                gameRepository.setCurrentElement(gameRepository.getSecondaryTabElementId())
            }
        }
    }
}