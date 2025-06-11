package com.rejnek.oog.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    fun onLoadAssetGameClicked() {
        viewModelScope.launch {
            gameRepository.initializeGameElement()
        }
    }
}
