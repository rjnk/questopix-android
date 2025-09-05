package com.rejnek.oog.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rejnek.oog.data.model.GamePackage
import kotlinx.serialization.json.Json

class GameStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_storage", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val LIBRARY_GAMES_KEY = "library_games"
        private const val CURRENT_SAVED_GAME_ID_KEY = "current_saved_game_id"
    }

    // ========== LIBRARY OPERATIONS ==========

    /**
     * Add a game to the library
     */
    fun addGameToLibrary(gamePackage: GamePackage) {
        val currentGames = getLibraryGames().toMutableList()
        // Remove existing game with same ID if present
        currentGames.removeAll { it.getId() == gamePackage.getId() }
        currentGames.add(gamePackage)
        saveLibraryGames(currentGames)
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
     * Get a specific game by ID from library
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

        // If we're removing the currently saved game, clear the saved game reference
        if (getIdOfSavedGame() == gameId) {
            clearSavedGame()
        }
    }

    private fun saveLibraryGames(games: List<GamePackage>) {
        val gamesJson = json.encodeToString(games)
        sharedPreferences.edit(commit = true) {
            putString(LIBRARY_GAMES_KEY, gamesJson)
        }
    }

    // ========== SAVED GAME OPERATIONS ==========

    /**
     * Set which game is currently being played/saved
     */
    fun setSavedGameId(gameId: String) {
        sharedPreferences.edit(commit = true) {
            putString(CURRENT_SAVED_GAME_ID_KEY, gameId)
        }
    }

    /**
     * Get the currently saved game package
     */
    fun getSavedGamePackage(): GamePackage? {
        val savedGameId = getIdOfSavedGame()
        return if (savedGameId != null) {
            getGameById(savedGameId)
        } else {
            null
        }
    }

    /**
     * Get the ID of the currently saved game
     */
    fun getIdOfSavedGame(): String? {
        return sharedPreferences.getString(CURRENT_SAVED_GAME_ID_KEY, null)
    }

    /**
     * Check if there is a saved game
     */
    fun hasSavedGame(): Boolean {
        return getIdOfSavedGame() != null
    }

    /**
     * Clear the saved game reference
     */
    fun clearSavedGame() {
        sharedPreferences.edit(commit = true) {
            remove(CURRENT_SAVED_GAME_ID_KEY)
        }
    }
}
