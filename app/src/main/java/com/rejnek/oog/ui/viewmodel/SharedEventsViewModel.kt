package com.rejnek.oog.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for sharing UI events across top-level screens.
 *
 * Enables communication between Home and Library screens,
 * such as triggering game import from the home screen.
 */
class SharedEventsViewModel : ViewModel() {
    private val _requestImportGame = MutableStateFlow(false)
    val requestImportGame: StateFlow<Boolean> = _requestImportGame.asStateFlow()

    /** Signals that game import should be triggered in the library. */
    fun triggerImportGame() {
        _requestImportGame.value = true
    }

    /** Marks the import event as consumed. */
    fun consumeImportGame() {
        _requestImportGame.value = false
    }
}

