package com.rejnek.oog.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rejnek.oog.data.model.GamePackage
import kotlinx.serialization.json.Json

class GameStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_save_data", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val GAME_STATE_KEY = "game_state"
        private const val SAVE_TIMESTAMP_KEY = "save_timestamp"
        private const val LIBRARY_GAMES_KEY = "library_games"
    }

    /**
     * Save game state to local storage
     */
    fun saveGameState(gameStateJson: String) {
        sharedPreferences.edit(commit = true) {
            putString(GAME_STATE_KEY, gameStateJson)
            putLong(SAVE_TIMESTAMP_KEY, System.currentTimeMillis())
        }
    }

    /**
     * Load game state from local storage
     */
    fun loadGameState(): String? {
        return sharedPreferences.getString(GAME_STATE_KEY, null)
    }

    /**
     * Check if there is a saved game state
     */
    fun hasSavedGame(): Boolean {
        val savedState = sharedPreferences.getString(GAME_STATE_KEY, null)
        return !savedState.isNullOrEmpty()
    }

    /**
     * Clear saved game state
     */
    fun clearSavedGame() {
        sharedPreferences.edit(commit = true) {
            remove(GAME_STATE_KEY)
            remove(SAVE_TIMESTAMP_KEY)
        }
    }

    /**
     * Add a new game to the library
     */
    fun addGameToLibrary(gamePackage: GamePackage): String {
        val currentGames = getLibraryGames().toMutableList()
        currentGames.add(gamePackage)
        saveLibraryGames(currentGames)

        return gamePackage.getId()
    }

    /**
     * Get all games in the library
     */
    fun getLibraryGames(): List<GamePackage> {
        val gamesJson = sharedPreferences.getString(LIBRARY_GAMES_KEY, null)
        return if (gamesJson != null) {
            try {
                json.decodeFromString<List<GamePackage>>(gamesJson)
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Get a specific game by ID
     */
    fun getGameById(gameId: String): GamePackage? {
        return getLibraryGames().find { it.getId() == gameId }
    }

    /**
     * Remove a game from the library
     */
    fun removeGameFromLibrary(gameId: String) {
        val games = getLibraryGames().toMutableList()
        games.removeAll { it.getId() == gameId }
        saveLibraryGames(games)
    }

    private fun saveLibraryGames(games: List<GamePackage>) {
        val gamesJson = json.encodeToString(games)
        sharedPreferences.edit(commit = true) {
            putString(LIBRARY_GAMES_KEY, gamesJson)
        }
    }
}
