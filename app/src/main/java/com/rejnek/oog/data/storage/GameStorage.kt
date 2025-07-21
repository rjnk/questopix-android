package com.rejnek.oog.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class GameStorage(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_save_data", Context.MODE_PRIVATE)

    companion object {
        private const val GAME_STATE_KEY = "game_state"
        private const val SAVE_TIMESTAMP_KEY = "save_timestamp"
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
}
