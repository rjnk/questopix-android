package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared UI events across top-level screens (Home <-> Library)
 */
class SharedEventsViewModel : ViewModel() {
    private val _requestImportGame = MutableStateFlow(false)
    val requestImportGame: StateFlow<Boolean> = _requestImportGame.asStateFlow()

    fun triggerImportGame() {
        _requestImportGame.value = true
    }

    fun consumeImportGame() {
        _requestImportGame.value = false
    }
}

