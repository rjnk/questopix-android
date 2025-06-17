package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.rejnek.oog.data.model.GameElementType
import com.rejnek.oog.data.repository.GameRepository.ButtonState
import com.rejnek.oog.data.repository.GameRepository.QuestionState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameTaskViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description = _description.asStateFlow()

    // Changed from a single button state to a list of buttons
    private val _buttons = MutableStateFlow<List<ButtonState>>(emptyList())
    val buttons = _buttons.asStateFlow()

    // Add state for question to expose to UI
    private val _questionState = MutableStateFlow<QuestionState?>(null)
    val questionState = _questionState.asStateFlow()

    // Text field value for answer input
    private val _answerText = MutableStateFlow("")
    val answerText = _answerText.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        // Observe buttons from repository - in its own coroutine
        viewModelScope.launch {
            gameRepository.buttons.collect { buttons ->
                if(gameRepository.currentElement.value.elementType == GameElementType.FINISH) {
                    return@collect
                }
                _buttons.value = buttons
            }
        }

        // Observe questions from repository
        viewModelScope.launch {
            gameRepository.questionState.collect { question ->
                if(gameRepository.currentElement.value.elementType == GameElementType.FINISH) {
                    return@collect
                }
                _questionState.value = question
            }
        }

        // Observe current element changes in a separate coroutine
        viewModelScope.launch {
            gameRepository.currentElement.collect { elem ->
                if(elem.elementType == GameElementType.FINISH) {
                    _navigationEvents.emit(NavigationEvent.Finish)
                    return@collect
                }
                else{
                    _name.value = elem.name
                    _description.value = elem.description
                }
            }
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            gameRepository.executeOnContinue(null)
        }
    }

    /**
     * Handle JS button click for a specific button
     */
    fun onJsButtonClicked(buttonId: Int) {
        viewModelScope.launch {
            gameRepository.executeButtonAction(buttonId)
        }
    }

    /**
     * Update answer text as user types
     */
    fun onAnswerTextChanged(text: String) {
        _answerText.value = text
    }

    /**
     * Submit answer to current question
     */
    fun submitAnswer() {
        val currentAnswer = _answerText.value.trim()
        if (currentAnswer.isNotEmpty()) {
            viewModelScope.launch {
                gameRepository.submitAnswer(currentAnswer)
            }
        }
    }

    // In GameNavigationTextViewModel
    sealed class NavigationEvent {
        object Finish : NavigationEvent()
    }
}
