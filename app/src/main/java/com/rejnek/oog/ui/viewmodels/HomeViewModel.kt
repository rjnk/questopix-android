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
    private val _hasSavedGame = MutableStateFlow(false)
    val hasSavedGame = _hasSavedGame.asStateFlow()

    init {
        viewModelScope.launch {
            _hasSavedGame.value = gameRepository.gameStorageRepository.hasSavedGame()
            gameRepository.initialize()
        }
    }

    fun onLoadSavedClicked() {
        viewModelScope.launch {
            gameRepository.loadSavedGame()
        }
    }
}
