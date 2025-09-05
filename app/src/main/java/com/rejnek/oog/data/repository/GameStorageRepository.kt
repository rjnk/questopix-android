package com.rejnek.oog.data.repository

import android.content.Context
import com.rejnek.oog.data.model.GamePackage
import com.rejnek.oog.data.storage.GameStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository responsible for game storage operations
 */
class GameStorageRepository(
    context: Context
) {
    private val gameStorage = GameStorage(context)

    // Library Operations
    fun addGameToLibrary(gamePackage: GamePackage) {
        gameStorage.addGameToLibrary(gamePackage)
    }

    fun removeGameFromLibrary(gameId: String) {
        gameStorage.removeGameFromLibrary(gameId)
    }

    fun getLibraryGames() = gameStorage.getLibraryGames()

    // Game Operations
    suspend fun saveGame(gamePackage: GamePackage): Unit = withContext(Dispatchers.IO) {
        // Update/add the game in the library with current state
        gameStorage.addGameToLibrary(gamePackage)
        // Mark this game as the currently saved one
        gameStorage.setSavedGameId(gamePackage.getId())
    }

    suspend fun getGameById(gameId: String): GamePackage? = withContext(Dispatchers.IO) {
        return@withContext gameStorage.getGameById(gameId)
    }

    suspend fun clearSavedGame(): Unit = withContext(Dispatchers.IO) {
        gameStorage.clearSavedGame()
    }

    suspend fun getIdOfSavedGame(): String? = withContext(Dispatchers.IO) {
        return@withContext gameStorage.getIdOfSavedGame()
    }

    suspend fun hasSavedGame(): Boolean = withContext(Dispatchers.IO) {
        return@withContext gameStorage.hasSavedGame()
    }

    suspend fun getSavedGamePackage(): GamePackage? = withContext(Dispatchers.IO) {
        return@withContext gameStorage.getSavedGamePackage()
    }
}
