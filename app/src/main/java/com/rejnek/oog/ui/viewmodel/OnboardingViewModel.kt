package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing first-time user onboarding state.
 *
 * Handles initial setup by preloading bundled games and tracking
 * whether the user has completed the onboarding flow.
 *
 * @param repository Game repository for accessing storage and preloading games
 */
class OnboardingViewModel(
    private val repository: GameRepository,
) : ViewModel() {
    private val _showOnboarding = MutableStateFlow(false)

    /** Whether the onboarding screen should be displayed. */
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

    /** Marks onboarding as complete and triggers navigation to home screen. */
    fun onOnboardingComplete() {
        _showOnboarding.value = false
    }
}
