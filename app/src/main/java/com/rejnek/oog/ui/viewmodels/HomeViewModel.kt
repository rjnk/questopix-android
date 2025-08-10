package com.rejnek.oog.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private var jsInitialized = false
    private val TAG = "HomeViewModel"

    private val _hasSavedGame = MutableStateFlow<Boolean>(false)
    val hasSavedGame = _hasSavedGame.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.jsEngine.initialize(gameRepository)
                .onSuccess {
                    jsInitialized = true
                    Log.d(TAG, "JS engine initialized successfully")

                    _hasSavedGame.value = gameRepository.hasSavedGame()
                }
        }
    }

    fun onLoadCustomGameFile(gameCode: String) {
        viewModelScope.launch {
            if (!jsInitialized) {
                Log.e(TAG, "JavaScript engine not ready")
                return@launch
            }

            try {
                gameRepository.initializeGameWithCode(gameCode)
                Log.d(TAG, "Custom game loaded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading custom game file", e)
            }
        }
    }

    fun onLoadSavedClicked() {
        viewModelScope.launch {
            gameRepository.loadSavedGame()
        }
    }
}
