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

class GameNavigationTextViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description = _description.asStateFlow()

    private val _currentElementType = MutableStateFlow<GameElementType>(GameElementType.UNKNOWN)
    val currentElementType = _currentElementType.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.currentGame.collectLatest { updatedGame ->
                updatedGame?.let { game ->
                    Log.d("GameViewModel", "Game updated: ${game.currentElement.name}, index: ${game.currentElementIndex}")
                    _name.value = game.currentElement.name
                    _description.value = game.currentElement.description
                    _currentElementType.value = game.currentElement.elementType
                }
            }
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            gameRepository.executeOnContinue(null)
        }
    }
}
