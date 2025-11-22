/*
 * Created with Github Copilot
 */
package com.rejnek.oog.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the settings screen.
 *
 * Tracks current game state for in-game settings display.
 * Provides game control actions (pause/quit) and external link handling.
 *
 * @param gameRepository Repository for game state and control operations
 */
class SettingsViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private val _openFromGame = MutableStateFlow(false)
    val openFromGame = _openFromGame.asStateFlow()

    private val _gameName = MutableStateFlow<String?>(null)
    val gameName = _gameName.asStateFlow()

    private val _gameDescription = MutableStateFlow<String?>(null)
    val gameDescription = _gameDescription.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.currentGamePackage.collect { pack ->
                if(pack != null) {
                    _openFromGame.value = true
                    _gameName.value = pack.getName()
                    _gameDescription.value = pack.getDescription()
                } else {
                    _openFromGame.value = false
                }
            }
        }
    }

    /** Pauses the current game and saves its state. */
    fun pauseGame() {
        viewModelScope.launch {
            delay(400) // Small delay to allow UI to update
            gameRepository.pauseCurrentGame()
        }
    }

    /** Quits the current game and discards progress. */
    fun quitGame() {
        viewModelScope.launch {
            gameRepository.quitCurrentGame()
        }
    }

    /** Opens a URL in the system browser. */
    fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}