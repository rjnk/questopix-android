package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameFinishViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    // UI state
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description = _description.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.selectedElement.collect { elem ->
                _name.value = elem?.name ?: "Err"
                _description.value = elem?.name ?: "Err"
            }
        }
    }


    fun onBackToHomeClicked() {
        gameRepository.cleanup()
    }
}

