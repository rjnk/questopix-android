package com.rejnek.oog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _GamesPackage = MutableStateFlow<List<GamePackage>>(emptyList())
    val libraryGames = _GamesPackage.asStateFlow()

    init {
        loadLibraryGames()
    }

    private fun loadLibraryGames() {
        viewModelScope.launch {
            _GamesPackage.value = gameRepository.getLibraryGames()
        }
    }

    fun onGameSelected(gameId: String, onGameStarted: () -> Unit) {
        viewModelScope.launch {
            gameRepository.initializeGameFromLibrary(gameId)
            onGameStarted()
        }
    }

    fun onAddGameFromFile(gameCode: String) {
        viewModelScope.launch {
            // Extract game name from the JavaScript code (look for a game title or use timestamp)
            val gameName = extractGameName(gameCode) ?: "Imported Game ${System.currentTimeMillis()}"

            // Add to library
            gameRepository.addGameToLibrary(gameName, gameCode)

            // Refresh the library list
            loadLibraryGames()
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

    fun refreshLibrary() {
        loadLibraryGames()
    }
}
