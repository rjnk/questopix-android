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
import com.rejnek.oog.data.repository.GameRepository.ButtonState
import com.rejnek.oog.data.repository.GameRepository.QuestionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect

class GameNavigationTextViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _name = MutableStateFlow("Loading...")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("Loading...")
    val description = _description.asStateFlow()

    private val _currentElementType = MutableStateFlow<GameElementType>(GameElementType.UNKNOWN)
    val currentElementType = _currentElementType.asStateFlow()

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
        refresh()

        // Observe buttons from repository - in its own coroutine
        viewModelScope.launch {
            gameRepository.buttons.collect { buttons ->
                _buttons.value = buttons
            }
        }

        // Observe questions from repository
        viewModelScope.launch {
            gameRepository.questionState.collect { question ->
                _questionState.value = question
                if (question == null) {
                    // Reset answer text when question is cleared
                    _answerText.value = ""
                }
            }
        }

        // Observe current element changes in a separate coroutine
        viewModelScope.launch {
            gameRepository.currentElement.collect { elem ->
                if (elem != null) {
                    _name.value = elem.name
                    _description.value = elem.description
                    _currentElementType.value = elem.elementType
                    Log.d("GameNavigationTextViewModel", "Element updated from flow: ${elem.name}")
                }
            }
        }
    }

    fun refresh(){
        _name.value = gameRepository.currentElement.value?.name ?: "Loading..."
        _description.value = gameRepository.currentElement.value?.description ?: "Loading..."

        Log.d("GameNavigationTextViewModel", "UI refreshed")
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            Log.d("GameNavigationTextViewModel", "onContinueClicked called")

            // run the onContinue script for the current game element
            gameRepository.executeOnContinue(null)

            Log.d("GameNavigationTextViewModel", gameRepository.currentElement.value?.name ?: "No current element")

            delay(10L) // TODO find a better way for RC

            // Check if the current element is a finish element
            if(gameRepository.currentElement.value?.elementType == GameElementType.FINISH){
                _navigationEvents.emit(NavigationEvent.Finish)
            }
            // Update the UI
            else{
                refresh()
            }
        }
    }

    /**
     * Handle JS button click for a specific button
     */
    fun onJsButtonClicked(buttonId: Int) {
        viewModelScope.launch {
            gameRepository.executeButtonAction(buttonId)
            refresh()
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
