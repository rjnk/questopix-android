package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val _showOnboarding = MutableStateFlow(false)
    val showOnboarding = _showOnboarding.asStateFlow()

    init {
        if (repository.storageRepository.isSetupComplete()) {
            _showOnboarding.value = false
        } else {
            _showOnboarding.value = true
            viewModelScope.launch {
                repository.preloadGames()
                repository.storageRepository.setSetupComplete()
            }
        }
    }

    fun onOnboardingComplete() {
        _showOnboarding.value = false
    }
}
