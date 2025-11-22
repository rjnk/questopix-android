package com.rejnek.oog.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rejnek.oog.data.model.GamePackage
import kotlinx.serialization.json.Json

/**
* Class to manage game storage using SharedPreferences.
*
* @param context Application context
*/
class GameStorage(context: Context) {
    companion object {
        private const val LIBRARY_GAMES_KEY = "library_games"
        private const val IS_SETUP_COMPLETE_KEY = "is_setup_complete"
    }
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_storage", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    // ========== LIBRARY OPERATIONS ==========

    /**
     * Add a game to the library
     * @param gamePackage GamePackage object to add
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
     * @return List of GamePackage objects in the library
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
     *
     * @param gameId ID of the game to retrieve
     * @return GamePackage object if found, null otherwise
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

    // ========== SETUP OPERATIONS ==========

    /**
     * Mark the initial setup as complete
     */
    fun setSetupComplete() {
        sharedPreferences.edit(commit = true) {
            putBoolean(IS_SETUP_COMPLETE_KEY, true)
        }
    }

    /**
     * Check if the initial setup is complete
     */
    fun isSetupComplete(): Boolean {
        return sharedPreferences.getBoolean(IS_SETUP_COMPLETE_KEY, false)
    }
}
