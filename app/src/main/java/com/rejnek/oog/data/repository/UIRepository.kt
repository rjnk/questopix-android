/*
 * Created with Github Copilot
 */
package com.rejnek.oog.data.repository

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.runtime.staticCompositionLocalOf

// Centralized capture mode flag (true only during off-screen screenshot rendering)
// This is used by the Share button
val LocalCaptureMode = staticCompositionLocalOf { false }

// Registry of composables to exclude from capture (e.g., Share, Finish buttons)
// This is used by the Share button to avoid capturing certain UI elements.
object UiCaptureExclusions {
    val excluded = mutableSetOf<@Composable () -> Unit>()
}

/**
 * Repository responsible for managing UI state and elements
 */
class GameUIRepository {

    // UI elements that are rendered for the current task
    private val _uiElements = MutableStateFlow<List<@Composable () -> Unit>>(emptyList())
    val uiElements: StateFlow<List<@Composable () -> Unit>> = _uiElements.asStateFlow()

    /**
     * Add a UI element (Composable function) to be displayed in the GameTaskScreen
     * @param element Composable function representing the UI element
     */
    fun addUIElement(element: @Composable () -> Unit) {
        _uiElements.value = _uiElements.value + element
    }

    /**
     * Remove the last added UI element
     */
    fun removeLastUIElement() {
        if (_uiElements.value.isNotEmpty()) {
            _uiElements.value = _uiElements.value.dropLast(1)
        }
    }

    /**
     * Clear all UI elements
     */
    fun clearUIElements() {
        _uiElements.value = emptyList()
        UiCaptureExclusions.excluded.clear()
    }
}
