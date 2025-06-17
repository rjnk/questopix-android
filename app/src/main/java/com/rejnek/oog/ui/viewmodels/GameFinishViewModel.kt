package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.data.repository.GameRepository
import com.rejnek.oog.ui.viewmodels.GameTaskViewModel.NavigationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            gameRepository.currentElement.collect { elem ->
                if (elem != null) {
                    _name.value = elem.name
                    _description.value = elem.description
                }
            }
        }
    }


    fun onBackToHomeClicked() {
        gameRepository.cleanup()
    }
}
