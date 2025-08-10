package com.rejnek.oog.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rejnek.oog.data.model.LibraryGame
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID

class GameStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_save_data", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val GAME_STATE_KEY = "game_state"
        private const val SAVE_TIMESTAMP_KEY = "save_timestamp"
        private const val CURRENT_GAME_ID_KEY = "current_game_id"
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
    fun addGameToLibrary(gameName: String, gameCode: String): String {
        val gameId = UUID.randomUUID().toString()
        val newGame = LibraryGame(
            id = gameId,
            name = gameName,
            gameCode = gameCode,
            importedAt = System.currentTimeMillis()
        )

        val currentGames = getLibraryGames().toMutableList()
        currentGames.add(newGame)
        saveLibraryGames(currentGames)

        return gameId
    }

    /**
     * Get all games in the library
     */
    fun getLibraryGames(): List<LibraryGame> {
        val gamesJson = sharedPreferences.getString(LIBRARY_GAMES_KEY, null)
        return if (gamesJson != null) {
            try {
                json.decodeFromString<List<LibraryGame>>(gamesJson)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Get a specific game by ID
     */
    fun getGameById(gameId: String): LibraryGame? {
        return getLibraryGames().find { it.id == gameId }
    }

    /**
     * Remove a game from the library
     */
    fun removeGameFromLibrary(gameId: String) {
        val games = getLibraryGames().toMutableList()
        games.removeAll { it.id == gameId }
        saveLibraryGames(games)
    }

    private fun saveLibraryGames(games: List<LibraryGame>) {
        val gamesJson = json.encodeToString(games)
        sharedPreferences.edit(commit = true) {
            putString(LIBRARY_GAMES_KEY, gamesJson)
        }
    }
}
