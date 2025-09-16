package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoadBundledViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        if(repository.storageRepository.isSetupComplete()) {
            _isLoading.value = false
        }
        else {
            viewModelScope.launch {
                repository.preloadGames()
                repository.storageRepository.setSetupComplete()
                _isLoading.value = false
            }
        }
    }
}