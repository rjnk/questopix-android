package com.rejnek.oog.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
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

    fun onLoadCustomGameFile(gamePackage: GamePackage) {
        viewModelScope.launch {
            if (!jsInitialized) {
                Log.e(TAG, "JavaScript engine not ready")
                return@launch
            }

            try {
                // Add to library first
                gameRepository.addGameToLibrary(gamePackage)

                // Then initialize and start the game
                gameRepository.initializeGameFromLibrary(gamePackage.getId())
                Log.d(TAG, "Custom game '${gamePackage.getName()}' added to library and loaded successfully")
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
