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
    }

    private fun saveLibraryGames(games: List<GamePackage>) {
        val gamesJson = json.encodeToString(games)
        sharedPreferences.edit(commit = true) {
            putString(LIBRARY_GAMES_KEY, gamesJson)
        }
    }
}
