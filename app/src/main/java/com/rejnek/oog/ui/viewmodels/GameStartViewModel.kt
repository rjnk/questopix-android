package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rejnek.oog.data.model.Coordinates
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameStartViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _heading = MutableStateFlow("Loading...")
    val heading: StateFlow<String> = _heading.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description: StateFlow<String> = _description.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.currentElement.collectLatest { updatedElem ->
                updatedElem.let { elem ->
                    Log.d("GameViewModel", "Element updated: ${elem.name}, type: ${elem.elementType}")
                    if(elem.elementType == GameElementType.START) {
                        _heading.value = elem.name
                        _description.value = elem.description
                    }
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
