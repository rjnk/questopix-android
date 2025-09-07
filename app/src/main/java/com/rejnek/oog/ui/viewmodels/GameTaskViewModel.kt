package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GameState
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asSharedFlow

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _finishGame = MutableStateFlow(false)
    val finishGame = _finishGame.asSharedFlow()

    private val _gameName = MutableStateFlow("")
    val gameName = _gameName.asStateFlow()

    // Expose UI elements from the repository
    val uiElements = gameRepository.gameUIRepository.uiElements


    init {
        viewModelScope.launch {
            gameRepository.currentGamePackage.collect { pack ->
                _gameName.value = pack?.getName() ?: ""

                if (pack?.state == GameState.FINISHED) {
                    _finishGame.emit(true)
                }
            }
        }
    }
}


