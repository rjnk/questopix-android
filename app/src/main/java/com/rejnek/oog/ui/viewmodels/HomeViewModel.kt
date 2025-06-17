package com.rejnek.oog.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private var jsInitialized = false
    private val TAG = "HomeViewModel"

    init {
        viewModelScope.launch {
            gameRepository.jsEngine.initialize()
                .onSuccess {
                    jsInitialized = true
                    Log.d(TAG, "JS engine initialized successfully")
                }
        }
    }

    fun onLoadAssetGameClicked() {
        viewModelScope.launch {
            if (!jsInitialized) {
                Log.e(TAG, "JavaScript engine not ready")
                return@launch
            }

            gameRepository.initializeGameElement()
        }
    }
}
