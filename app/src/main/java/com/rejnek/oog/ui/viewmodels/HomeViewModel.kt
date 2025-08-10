package com.rejnek.oog.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {
    private var jsInitialized = false
    private val TAG = "HomeViewModel"

    private val _hasSavedGame = MutableStateFlow<Boolean>(false)
    val hasSavedGame = _hasSavedGame.asStateFlow()

    init {
        viewModelScope.launch {
            gameRepository.jsEngine.initialize(gameRepository)
                .onSuccess {
                    jsInitialized = true
                    Log.d(TAG, "JS engine initialized successfully")

                    _hasSavedGame.value = gameRepository.hasSavedGame()
                }
        }
    }

    fun onLoadCustomGameFile(gameCode: String) {
        viewModelScope.launch {
            if (!jsInitialized) {
                Log.e(TAG, "JavaScript engine not ready")
                return@launch
            }

            try {
                // Extract game name from the JavaScript code (look for a game title or use timestamp)
                val gameName = extractGameName(gameCode) ?: "Imported Game ${System.currentTimeMillis()}"

                // Add to library first
                val gameId = gameRepository.addGameToLibrary(gameName, gameCode)

                // Then initialize and start the game
                gameRepository.initializeGameFromLibrary(gameId)
                Log.d(TAG, "Custom game '$gameName' added to library and loaded successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading custom game file", e)
            }
        }
    }

    private fun extractGameName(gameCode: String): String? {
        // Try to extract game name from common patterns in JavaScript
        val patterns = listOf(
            Regex("""name\s*[:=]\s*["']([^"']+)["']"""),
            Regex("""title\s*[:=]\s*["']([^"']+)["']"""),
            Regex("""gameName\s*[:=]\s*["']([^"']+)["']"""),
            Regex("""gameTitle\s*[:=]\s*["']([^"']+)["']""")
        )

        for (pattern in patterns) {
            val match = pattern.find(gameCode)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return null
    }

    fun onLoadSavedClicked() {
        viewModelScope.launch {
            gameRepository.loadSavedGame()
        }
    }
}
